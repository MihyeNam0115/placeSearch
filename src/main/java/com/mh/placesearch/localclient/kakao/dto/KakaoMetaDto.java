package com.mh.placesearch.localclient.kakao.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class KakaoMetaDto {
    private Integer totalCount;
    private Integer pageableCount;
    private Boolean isEnd;
    private KakaoRegionInfoDto sameName;
}
