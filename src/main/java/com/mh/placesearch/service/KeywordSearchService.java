package com.mh.placesearch.service;

import com.mh.placesearch.searchservice.SearchService;
import lombok.Builder;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeywordSearchService {

    private final List<SearchService> searchEngines;

    public KeywordSearchService(final List<SearchService> searchEgnines) {
        this.searchEngines = searchEgnines;
    }

    public List<String> searchByKeyword(String keyword) {

        List<SearchResult> searchResults = searchAndGetherByKeyword(keyword);

        List<String> orderedPlaces = sortAndDistinctSearchResults(searchResults);

        return orderedPlaces;
    }

    private List<SearchResult> searchAndGetherByKeyword(String keyword) {
        List<SearchResult> searchResults = new ArrayList<>();

        for (SearchService searchService : searchEngines) {
            int enginePriority = searchService.getPriority();
            List<String> places = searchService.searchByKeyword(keyword);

            searchResults.add(SearchResult.builder()
                    .priority(enginePriority)
                    .places(places)
                    .build());
        }
        return searchResults;
    }

    private List<String> sortAndDistinctSearchResults(List<SearchResult> searchResults) {
        List<List<String>> orderedSearchResults = flatAndOrderedSearchResult(searchResults);

        Map<String, Integer> placeAndCountMap = new LinkedHashMap<>();

        for (List<String> places : orderedSearchResults) {
            for (String place : places) {
                Integer count = placeAndCountMap.get(place);
                if (count == null) {
                    placeAndCountMap.put(place, 1);
                } else {
                    placeAndCountMap.put(place, ++count);
                }
            }
        }

        List<String> result = placeAndCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(o -> o.getKey())
                .collect(Collectors.toList());

        return result;
    }

    private List<List<String>> flatAndOrderedSearchResult(List<SearchResult> searchResults) {
        Map<Integer, List<List<String>>> sortedResultsMap = new TreeMap<>();

        for (SearchResult searchResult : searchResults) {
            int enginePriority = searchResult.getPriority();
            List<String> places = searchResult.getPlaces();

            List<List<String>> sortedResults = sortedResultsMap.get(enginePriority);
            if (sortedResults == null) {
                sortedResults = new ArrayList<>();
                sortedResultsMap.put(enginePriority, sortedResults);
            }
            sortedResults.add(places);
        }

        return sortedResultsMap.values().stream().flatMap(o -> o.stream()).collect(Collectors.toList());
    }

    @Builder
    @Value
    public static class SearchResult {
        int priority;
        List<String> places;
    }
}
