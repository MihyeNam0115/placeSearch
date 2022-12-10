package com.mh.placesearch.localclient.naver;

import com.mh.placesearch.localclient.naver.dto.NaverLocalKeywordSearchDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverLocalClient", url = "https://openapi.naver.com")
public interface NaverLocalClient {
    @GetMapping(
            value = "/v1/search/local.json",
            headers = {"X-Naver-Client-Id=EE5xUQwtxI1xyiT6la9b", "X-Naver-Client-Secret=lkzGA22Gq7"})
    NaverLocalKeywordSearchDto searchByKeyword(
            @RequestParam String query, @RequestParam Integer display);
}
