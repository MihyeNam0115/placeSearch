package com.mh.placesearch.controller.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Value
public class PlaceSearchResultDto {
    List<PlaceDto> places;
}
