package com.mh.placesearch.controller.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class KeywordCountDto {
    private String keyword;
    private Long count;
}
