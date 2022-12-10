package com.mh.placesearch.localclient.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoRegionInfoDto {
    List<String> region;
    String keyword;
    String selectedRegion;
}
