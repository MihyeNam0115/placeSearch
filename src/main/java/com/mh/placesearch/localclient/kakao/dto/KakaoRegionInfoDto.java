package com.mh.placesearch.localclient.kakao.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Value
public class KakaoRegionInfoDto {
    List<String> region;
    String keyword;
    String selectedRegion;
}
