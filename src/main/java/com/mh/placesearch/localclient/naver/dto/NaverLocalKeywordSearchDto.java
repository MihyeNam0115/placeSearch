package com.mh.placesearch.localclient.naver.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Value
public class NaverLocalKeywordSearchDto {
    private String lastBuildDate;
    private String total;
    private String start;
    private String display;
    private List<NaverItemDto> items;
}
