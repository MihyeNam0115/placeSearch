package com.mh.placesearch.localclient.kakao;

import com.mh.placesearch.localclient.kakao.dto.KakaoLocalKeywordSearchDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoLocalClient"
        , url = "https://dapi.kakao.com"
        , fallbackFactory = KakaoLocalClientFallbackFactory.class)
public interface KakaoLocalClient {
    @GetMapping(
            value = "/v2/local/search/keyword.json",
            headers = "Authorization=KakaoAK d444bf159502acc9926acb04f9da3408")
    KakaoLocalKeywordSearchDto searchByKeyword(
            @RequestParam String query, @RequestParam Integer size);
}
