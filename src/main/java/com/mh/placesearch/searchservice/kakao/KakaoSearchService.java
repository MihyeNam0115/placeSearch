package com.mh.placesearch.searchservice.kakao;

import com.mh.placesearch.searchservice.SearchService;

import java.util.ArrayList;
import java.util.List;

public class KakaoSearchService implements SearchService {

    private final int priority = 0;

    @Override
    public List<String> searchByKeyword(String keyword) {
        List<String> places = new ArrayList<>();
        places.add("abc");
        return places;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
