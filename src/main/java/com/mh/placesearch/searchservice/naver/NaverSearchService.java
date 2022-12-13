package com.mh.placesearch.searchservice.naver;

import com.mh.placesearch.localclient.naver.NaverLocalClient;
import com.mh.placesearch.localclient.naver.dto.NaverItemDto;
import com.mh.placesearch.localclient.naver.dto.NaverLocalKeywordSearchDto;
import com.mh.placesearch.searchservice.SearchService;
import com.mh.placesearch.searchservice.WhitespaceAndHtmlRemover;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NaverSearchService implements SearchService {

    private static final int MAX_COUNT = 10;

    private final int priority = 1;
    private final NaverLocalClient naverLocalClient;

    public NaverSearchService(NaverLocalClient naverLocalClient) {
        this.naverLocalClient = naverLocalClient;
        System.out.println("Loading NaverSearchService");
    }


    @Override
    public List<String> searchByKeyword(String keyword) {
        if (ObjectUtils.isEmpty(keyword)) {
            return Collections.emptyList();
        }

        try {
            NaverLocalKeywordSearchDto result = this.naverLocalClient.searchByKeyword(keyword, MAX_COUNT);
            log.debug(result.toString());

            return result.getItems().stream()
                    .map(NaverItemDto::getTitle)
                    .map(WhitespaceAndHtmlRemover::apply)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error(ex.toString());
            return Collections.emptyList();
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
