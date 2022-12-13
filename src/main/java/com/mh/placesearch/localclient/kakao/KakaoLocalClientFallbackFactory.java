package com.mh.placesearch.localclient.kakao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaoLocalClientFallbackFactory implements FallbackFactory<KakaoLocalClientFallback> {
    @Override
    public KakaoLocalClientFallback create(Throwable cause) {
        log.debug("KakaoLocalClientFallbackFactory - create : {}", cause == null ? "null" : cause.toString());
        return new KakaoLocalClientFallback();
    }
}
