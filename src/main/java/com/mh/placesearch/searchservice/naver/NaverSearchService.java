package com.mh.placesearch.searchservice.naver;

import com.mh.placesearch.searchservice.SearchService;

import java.util.Collections;
import java.util.List;

public class NaverSearchService implements SearchService {

    private final int priority = 1;

    @Override
    public List<String> searchByKeyword(String keyword) {
        return Collections.emptyList();
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
