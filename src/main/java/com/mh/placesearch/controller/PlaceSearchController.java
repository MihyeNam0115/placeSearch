package com.mh.placesearch.controller;

import com.mh.placesearch.controller.dto.KeywordCountDto;
import com.mh.placesearch.controller.dto.KeywordRankingResultDto;
import com.mh.placesearch.controller.dto.PlaceDto;
import com.mh.placesearch.controller.dto.PlaceSearchResultDto;
import com.mh.placesearch.service.KeywordCount;
import com.mh.placesearch.service.KeywordRankingService;
import com.mh.placesearch.service.KeywordSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class PlaceSearchController {

    private final KeywordSearchService keywordSearchService;
    private final KeywordRankingService keywordRankingService;

    public PlaceSearchController(KeywordSearchService keywordSearchService, KeywordRankingService keywordRankingService) {
        this.keywordSearchService = keywordSearchService;
        this.keywordRankingService = keywordRankingService;
    }

    @GetMapping("/place")
    PlaceSearchResultDto searchByKeyword(@RequestParam("q") String query) {
        List<String> searchedPlaces = this.keywordSearchService.searchByKeyword(query);

        return toPlaceSearchResultDto(searchedPlaces);
    }

    private PlaceSearchResultDto toPlaceSearchResultDto(List<String> searchedPlaces) {
        List<PlaceDto> placeDtos = new ArrayList<>();
        for (String place : searchedPlaces) {
            placeDtos.add(PlaceDto.builder().title(place).build());
        }

        return PlaceSearchResultDto.builder()
                .places(placeDtos)
                .build();
    }

    @GetMapping("/keywordRanking")
    KeywordRankingResultDto keywordRanking() {
        List<KeywordCount> topNKeywords = this.keywordRankingService.getTopNKeywords();

        return toKeywordRankingResultDto(topNKeywords);
    }

    private KeywordRankingResultDto toKeywordRankingResultDto(List<KeywordCount> topNKeywords) {
        List<KeywordCountDto> keywords = new ArrayList<>();
        for(KeywordCount each : topNKeywords) {
            keywords.add(KeywordCountDto.builder()
                    .keyword(each.getKeyword())
                    .count(each.getCount())
                    .build()
            );
        }

        return KeywordRankingResultDto.builder().keywords(keywords).build();
    }
}
