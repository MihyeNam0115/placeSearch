package com.mh.placesearch.service;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class KeywordCount {
    private String keyword;
    private Long count;
}
