package com.mh.placesearch.searchservice;

import java.util.List;

public interface SearchService {

    List<String> searchByKeyword(String keyword);

    int getPriority();
}
