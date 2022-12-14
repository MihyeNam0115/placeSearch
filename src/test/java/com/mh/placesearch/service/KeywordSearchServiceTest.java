package com.mh.placesearch.service;

import com.mh.placesearch.searchservice.SearchService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordSearchServiceTest {
    KeywordRankingService keywordRankingService = new KeywordRankingService();

    public static SearchService newBuildService(List<String> result, int priority) {
        return new SearchService() {
            @Override
            public List<String> searchByKeyword(String keyword) {
                return result;
            }

            @Override
            public int getPriority() {
                return priority;
            }
        };
    }

    @Test
    void searchByKeyword_whenOneDuplicated_shouldSortedMostDuplicatedFirst() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("A곱창", "B곱창"), 1));
        searchServices.add(newBuildService(Arrays.asList("B곱창"), 2));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("곱창");
        assertThat(results).hasSize(2)
                .isEqualTo(Arrays.asList("B곱창", "A곱창"));
    }

    @Test
    void searchByKeyword_whenNoDuplicated_shouldSmallerPriorityValueServiceFirst() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("A곱창", "B곱창"), 2));
        searchServices.add(newBuildService(Arrays.asList("C곱창", "D곱창"), 1));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("곱창");
        assertThat(results).hasSize(4)
                .isEqualTo(Arrays.asList("C곱창", "D곱창", "A곱창", "B곱창"));
    }


    @Test
    void searchByKeyword_whenMixedCase1_shouldReturnOrderedAndDisticted() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("A곱창", "B곱창", "C곱창", "D곱창"), 1));
        searchServices.add(newBuildService(Arrays.asList("A곱창", "E곱창", "D곱창", "C곱창"), 2));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("곱창");
        assertThat(results).hasSize(5)
                .isEqualTo(Arrays.asList("A곱창", "C곱창", "D곱창", "B곱창", "E곱창"));
    }

    @Test
    void searchByKeyword_whenMixedCase2_shouldReturnOrderedAndDisticted() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("카카오뱅크", "우리은행", "국민은행", "부산은행", "새마을금고"), 1));
        searchServices.add(newBuildService(Arrays.asList("카카오뱅크", "부산은행", "하나은행", "국민은행", "기업은행"), 2));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("은행");
        assertThat(results).hasSize(7)
                .isEqualTo(Arrays.asList("카카오뱅크", "국민은행", "부산은행", "우리은행", "새마을금고", "하나은행", "기업은행"));
    }

    @Test
    void searchByKeyword_when6ItemsFromAService3ItemsFromBService_shouldReturn9Items() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6"), 1));
        searchServices.add(newBuildService(Arrays.asList("테스트_B1", "테스트_B2", "테스트_B3"), 2));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("테스트");
        assertThat(results).hasSize(9)
                .isEqualTo(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_B1", "테스트_B2", "테스트_B3"));
    }

    @Test
    void searchByKeyword_when7ItemsFromAService3ItemsFromBService_shouldReturn10Items() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_A7"), 1));
        searchServices.add(newBuildService(Arrays.asList("테스트_B1", "테스트_B2", "테스트_B3"), 2));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("테스트");
        assertThat(results).hasSize(10)
                .isEqualTo(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_A7", "테스트_B1", "테스트_B2", "테스트_B3"));
    }

    @Test
    void searchByKeyword_when9ItemsFromAService3ItemsFromBService_shouldReturn10Items() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_A7" , "테스트_A8", "테스트_A9"), 1));
        searchServices.add(newBuildService(Arrays.asList("테스트_B1", "테스트_B2", "테스트_B3"), 2));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("테스트");
        assertThat(results)
                .hasSize(10)
                .isEqualTo(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_A7", "테스트_B1", "테스트_B2", "테스트_B3"));
    }

    @Test
    void searchByKeyword_when9ItemsFromAService3ItemsFromBServiceWithDiffPriofiry_shouldReturn10Items() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_A7" , "테스트_A8", "테스트_A9"), 2));
        searchServices.add(newBuildService(Arrays.asList("테스트_B1", "테스트_B2", "테스트_B3"), 1));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("테스트");
        assertThat(results).hasSize(10)
                .isEqualTo(Arrays.asList("테스트_B1", "테스트_B2", "테스트_B3", "테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_A7"));
    }

    @Test
    void searchByKeyword_when9ItemsFromAService9ItemsFromBService_shouldReturn10Items() {
        List<SearchService> searchServices = new ArrayList<>();
        searchServices.add(newBuildService(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_A6", "테스트_A7" , "테스트_A8", "테스트_A9"), 1));
        searchServices.add(newBuildService(Arrays.asList("테스트_B1", "테스트_B2", "테스트_B3", "테스트_B4", "테스트_B5", "테스트_B6", "테스트_B7" , "테스트_B8", "테스트_B9"), 2));

        KeywordSearchService service = new KeywordSearchService(searchServices, keywordRankingService);
        List<String> results = service.searchByKeyword("테스트");
        assertThat(results).hasSize(10)
                .isEqualTo(Arrays.asList("테스트_A1", "테스트_A2", "테스트_A3", "테스트_A4", "테스트_A5", "테스트_B1", "테스트_B2", "테스트_B3", "테스트_B4", "테스트_B5"));
    }
}