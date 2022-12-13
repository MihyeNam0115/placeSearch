package com.mh.placesearch.localclient.kakao;

import com.mh.placesearch.localclient.kakao.dto.KakaoLocalKeywordSearchDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class KakaoLocalClientFallback implements KakaoLocalClient {

    @Override
    public KakaoLocalKeywordSearchDto searchByKeyword(String query, Integer size) {
        log.debug("KakaoLocalClientFallback - searchByKeyword : query={}, size={}", query, size);
        return KakaoLocalKeywordSearchDto.builder().meta(null).documents(Collections.emptyList()).build();
    }
}
