package com.mh.placesearch.localclient.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoMetaDto {
    private Integer totalCount;
    private Integer pageableCount;
    private Boolean isEnd;
    private KakaoRegionInfoDto sameName;
}
