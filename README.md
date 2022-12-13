# placeSearch

## 사용한 외부 라이브러리 및 각 라이버러리의 사용 목적과 선택 사유
- spring-boot-starter-actuator
 : 어플리케이션의 상태정보와 Health check 등의 기능을 제공하는 라이브러리입니다.
  - 사용 목적 : circuit breaker의 적용 상태를 확인하기 위해 적용했습니다. 아래 API를 호출하면 서버의 상태를 확인 할 수 있습니다. 
      > GET http://localhost:8080/actuator/health
  - 선택 사유 : Spring Boot Actuator는 Spring Boot project에서 아주 쉬운 설정으로 사용할수 있으며, 추후 모니터링 시스템과 연계시 Prometheus, Datadog 등 다양한 모니터링 시스템과도 쉽게 연결해서 사용할수 있습니다.
- spring-boot-starter-openfeign
 : HTTP API를 쉽게 호출할 수 있도록 만들어주는 HTTP client binder 입니다. 인터페이스를 만들고 어노테이션을 붙이는 방식으로 쉽고 빠르게 사용할수 있습니다.
  - 사용 목적 : Kakao Local API 및 Naver Local API의 연동을 쉽게 적용하기 위해 사용했습니다.
  - 선택 사유 : 기존 HttpURLConnection, Apache Http Client는 low level의 HTTP Client를 설정하고 사용할수 있었으나, 제대로 사용하기 위해서는 많은 것을 알야아 하며 코드도 복잡한 단점이 있었습니다. 그래서 더 생산성 높은 라이브러리를 사용하고 싶었고, RestTemplate 등의 유사 솔루션대비 사용이 간편하고 최근에 많이 사용하는 라이브러리라 생각되어 선택하게 됐습니다. 
- spring-cloud-starter-circuitbreaker-reactor-resilence4j
 : Circuit Breaker 등의 기능을 제공하는 resilence4j 라이브러리 입니다. 
  - 사용 목적 : resilience4j를 이용한 circuit breaker 기능 사용을 위해 사용했습니다.
  - 선택 사유 : openFeign에 사용할 수 있는 몇가지 circuit breaker 중 Hystrix와 고민하다 resilence4j를 선택했습니다. 그 이유는 Hystrix가 2018년에 신규개발을 중단하였고, 대신 resilence4j는 현재도 활발하게 개선이 되고 있습니다. 
- rest-assured 
 : RESTful API 의 테스트를 쉽게 할수 있도록 여러가지 기능을 제공하는 라이브러리 입니다.
  - 사용 목적 : 본 서비스의 RESTful API의 쉬운 테스트를 위해 사용했습니다.  
  - 선택 사유 : RESTful API 테스트를 위해서는 assertj 등의 기존 validation 라이브러리를 사용하기 위해서는 여러가지 귀찮은 과정이 필요합니다. 하지만 Rest Assured 라이브러리는 response 값을 별도의 Class로 만들지 않고도 쉽게 원하는 값에 접근 할수 있으며, RESTful API의 validation을 위한 여러가지 기능을 제공하고 있어서 사용했습니다. 

## 기술적 요구사항
### 1. 동시성 이슈가 발생할 수 있는 부분을 염두에 둔 설계 및 구현 (예시, 키워드 별로 검색된 횟수)
1. Search by Keyword API가 동시에 여러번 호출 되더라도 동시성 이슈가 발생하지 않도록 다음과 같은 처리 했습니다. 
- Keyword별 호출 회수를 기록하기 위해서 ConcurrentHashMap을 이용해서 동시성 이슈를 해결했습니다.
  동시에 여러곳에서 searchByKeyword를 호출 하더라도 ConcurrentHashMap의 compute 함수를 통해서 count를 증가시키는데, compute 함수 자체가 atomic하게 동작하므로 동시성 문제의 발생을 차단 했습니다. 
 > private final ConcurrentHashMap<String, Long> keywordCountMap;
- 많이 호출된 Keyword 리스트를 조회하기 위해 별도의 keyword 리스트를 만들었으며, 이는 불변 객체로 변경이 필요한 경우 새로운 객체를 만들어서 reference만 변경하도록 했습니다.
  이를 통해서 keyword 리스트 조회용 API(keywordRanking)를 여러번 호출하더라도 사용하고 있는 list가 변경될일 없게 되어 동시성 이슈를 차단했습니다.
 > private List<KeywordCount> topNKeywordsUnmodifiable;
- 또한 keyword 별 count 증가와 상위 keyword 리스트 생성을 하나의 Critical Section으로 만들기 위해 ReentrantLock을 사용했습니다.
```java
public boolean updateKeywordCountData(String keyword) {
    try {
        if (reentrantLock.tryLock(10, TimeUnit.SECONDS) == false) {
            return false;
        }
        Long count = increaseKeywordCountMap(keyword);
        updateTopNKeywords(keyword, count);
        //...
    } finally {
        reentrantLock.unlock();
    }

    return true;
}
 ```

### 2. 카카오, 네이버 등 검색 API 제공자의 "다양한" 장애 발생 상황에 대한 고려
> 해결책 : Resilience4j를 이용한 Circuit Breaker 적용

외부 서비스(카카오, 네이버 등)의 다양한 장애로 인한 응답 지연 또는 에러 발생으로 PlaceSearch 서비스의 전체 동작에 영향을 발생할 수 있습니다.
예를 들면 네이버 API의 응답지연이 발생할 경우, PlaceSearch의 대부분의 Thread 또는 Connection이 네이버 서버에 할당되어 응답을 대기하게 되고,
이로 인해 PlaceSearch 서버의 리소스 고갈로 더이상 요청을 처리 못 하게 되는 상황이 발생할 수 있습니다.
즉, PlaceSearch는 문제가 없는데, 외부 서비스의 장애 상황으로 PlaceSearch 서비스의 전면 장애가 발생하게 되는 것입니다.
이런 문제를 막기 위한 기능이 Circuit Breaker 이고, 이번 과제에서는 Resilence4j를 활요하여 쉽게 적용해 봤습니다.

### 3. 구글 장소 검색 등 새로운 검색 API 제공자의 추가 시 변경 영역 최소화에 대한 고려
> 외부 검색 서비스에 대한 Interface는 동일하게 유지하고, 외부 서비스별 실제 구현 로직을 분리하였습니다.

1. 외부 검색 서비스의 Interface 를 하나로 통일했습니다.
```java
public interface SearchService {

    List<String> searchByKeyword(String keyword);

    int getPriority();
}
```
2. 각 외부 서비스의 API의 처리를 담당할 서비스들을 분리하여 각 서비스에 맞는 구현을 추가할수 있도록 했습니다.
 - KakaoSearchService
 - NaverSearchService

3. 각 외부 서비스의 API Spec에 맞게 OpenFeign 라이브러리를 사용하여 쉽게 전용 API Client를 만들었습니다.
 - KakaoLocalClient
 - NaverLocalClient

4. 새로운 서비스 추가시 새로운 XXXSearchService 를 @Component annotation을 이용하여 등록하면, Spring의 DI 기능을 통해 자동으로 등록되도록 구성했습니다.
  아래 코드에서 "final List<SearchService> searchEgnines" parameter에 신규 SearchService가 자동으로 등록하게 됩니다.
  또한 그 이후의 로직도 문제 없이 동작하도록 코드를 작성했습니다.
```java
    public KeywordSearchService(final List<SearchService> searchEgnines, KeywordRankingService keywordRankingService) {
        this.searchEngines = searchEgnines;
        this.keywordRankingService = keywordRankingService;
    }
```


## API 사용 방법
1. SearchByKeyword API 
> GET /v1/place?q={keyword}

[cURL Example]
```curl
curl --location --request GET 'http://localhost:8080/v1/place?q={keyword}' 
```

2. Keyword Ranking API
> GET /v1/keywordRanking

[cURL Example]
```curl
curl --location --request GET 'http://localhost:8080/v1/keywordRanking'
```