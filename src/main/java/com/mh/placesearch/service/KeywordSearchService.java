package com.mh.placesearch.service;

import com.mh.placesearch.searchservice.SearchService;
import lombok.Builder;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeywordSearchService {

    private static final int DEFAULT_MAX_ITEM_COUNT_PER_SERVICE = 5;
    private final List<SearchService> searchEngines;
    private final KeywordRankingService keywordRankingService;

    public KeywordSearchService(final List<SearchService> searchEgnines, KeywordRankingService keywordRankingService) {
        this.searchEngines = searchEgnines;
        this.keywordRankingService = keywordRankingService;
    }

    public List<String> searchByKeyword(String keyword) {

        List<List<String>> orderedAllSearchResults = searchAndGetherByKeywordWithOrdering(keyword);
        List<List<String>> orderedSearchResults = applyMaxNCountPolicy(orderedAllSearchResults);

        List<String> orderedPlaces = applyDistinctPolicy(orderedSearchResults);

        return orderedPlaces;
    }

    private List<List<String>> searchAndGetherByKeywordWithOrdering(String keyword) {
        if (ObjectUtils.isEmpty(keyword)) {
            return Collections.emptyList();
        }

        if (!this.keywordRankingService.updateKeywordCountData(keyword)) {
            return Collections.emptyList();
        }

        List<SearchResult> searchResults = new ArrayList<>();

        for (SearchService searchService : searchEngines) {
            int enginePriority = searchService.getPriority();
            List<String> places = searchService.searchByKeyword(keyword);

            searchResults.add(SearchResult.builder()
                    .priority(enginePriority)
                    .places(places)
                    .build());
        }

        return sortByServerPriority(searchResults);
    }

    private List<List<String>> applyMaxNCountPolicy(List<List<String>> orgOrderedSearchItems) {
        int numberOfLower = 0;
        for(List<String> each : orgOrderedSearchItems) {
            if(each.size() < DEFAULT_MAX_ITEM_COUNT_PER_SERVICE) {
                numberOfLower = DEFAULT_MAX_ITEM_COUNT_PER_SERVICE - each.size();
            }
        }

        List<List<String>> orderedSearchItems = new ArrayList<>();
        for(List<String> each : orgOrderedSearchItems) {
            if( each.size() > DEFAULT_MAX_ITEM_COUNT_PER_SERVICE){
                int overCount = each.size() - DEFAULT_MAX_ITEM_COUNT_PER_SERVICE;
                if(overCount > numberOfLower) {
                    orderedSearchItems.add(each.subList(0, DEFAULT_MAX_ITEM_COUNT_PER_SERVICE + numberOfLower));
                    numberOfLower = 0;
                }else {
                    orderedSearchItems.add(each);
                    numberOfLower -= overCount;
                }
            }else{
                orderedSearchItems.add(each);
            }
        }

        return orderedSearchItems;
    }

    private List<String> applyDistinctPolicy(List<List<String>> orderedSearchResults) {

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

    private List<List<String>> sortByServerPriority(List<SearchResult> searchResults) {
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
