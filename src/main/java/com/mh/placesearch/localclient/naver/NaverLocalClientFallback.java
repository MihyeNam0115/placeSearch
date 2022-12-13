package com.mh.placesearch.localclient.naver;

import com.mh.placesearch.localclient.naver.dto.NaverLocalKeywordSearchDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class NaverLocalClientFallback implements NaverLocalClient {
    @Override
    public NaverLocalKeywordSearchDto searchByKeyword(String query, Integer size) {
        log.debug("NaverLocalClientFallback - searchByKeyword : query={}, size={}", query, size);
        return NaverLocalKeywordSearchDto.builder().items(Collections.emptyList()).build();
    }
}
