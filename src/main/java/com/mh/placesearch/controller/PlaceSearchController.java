package com.mh.placesearch.controller;

import com.mh.placesearch.controller.dto.PlaceDto;
import com.mh.placesearch.controller.dto.PlaceSearchResultDto;
import com.mh.placesearch.service.KeywordSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class PlaceSearchController {

    private final KeywordSearchService keywordSearchService;

    public PlaceSearchController(KeywordSearchService keywordSearchService) {
        this.keywordSearchService = keywordSearchService;
    }

    @GetMapping("/place")
    PlaceSearchResultDto searchByKeyword(@RequestParam("q") String query) {
        List<String> searchedPlaces = this.keywordSearchService.searchByKeyword(query);

        return toPlaceSearchResultDto(searchedPlaces);
    }

    private PlaceSearchResultDto toPlaceSearchResultDto(List<String> searchedPlaces) {
        List<PlaceDto> placeDtos = new ArrayList<>();
        for(String place : searchedPlaces) {
            placeDtos.add(PlaceDto.builder().title(place).build());
        }

        return PlaceSearchResultDto.builder()
                .places(placeDtos)
                .build();
    }
}
