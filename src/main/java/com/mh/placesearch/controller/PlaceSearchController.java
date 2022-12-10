package com.mh.placesearch.controller;

import com.mh.placesearch.controller.dto.PlaceDto;
import com.mh.placesearch.controller.dto.PlaceSearchResultDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/v1")
public class PlaceSearchController {

    @GetMapping("/place")
    PlaceSearchResultDto searchByKeyword(@RequestParam("q") String query) {

        return PlaceSearchResultDto.builder()
                .places(Arrays.asList(PlaceDto.builder().title(query).build()))
                .build();
    }
}
