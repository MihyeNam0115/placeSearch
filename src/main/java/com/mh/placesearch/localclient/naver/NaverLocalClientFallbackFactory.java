package com.mh.placesearch.localclient.naver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NaverLocalClientFallbackFactory implements FallbackFactory<NaverLocalClientFallback> {
    @Override
    public NaverLocalClientFallback create(Throwable cause) {
        log.debug("KakaoLocalClientFallbackFactory - create : {}", cause == null ? "null" : cause.toString());
        return new NaverLocalClientFallback();
    }
}
