package com.mh.placesearch.controller.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class ErrorResponseDto {
    private String code;
    private String message;
}
