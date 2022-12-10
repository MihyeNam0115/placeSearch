package com.mh.placesearch.searchservice.kakao;

import com.mh.placesearch.localclient.kakao.KakaoLocalClient;
import com.mh.placesearch.localclient.kakao.dto.KakaoDocumentDto;
import com.mh.placesearch.localclient.kakao.dto.KakaoLocalKeywordSearchDto;
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
public class KakaoSearchService implements SearchService {

    private static final int MAX_COUNT = 5;

    private final int priority = 0;
    private final KakaoLocalClient kakaoLocalClient;

    public KakaoSearchService(KakaoLocalClient kakaoLocalClient) {
        this.kakaoLocalClient = kakaoLocalClient;
        System.out.println("Loading KakaoSearchService");
    }

    @Override
    public List<String> searchByKeyword(String keyword) {
        if (ObjectUtils.isEmpty(keyword)) {
            return Collections.emptyList();
        }

        try {
            KakaoLocalKeywordSearchDto result = this.kakaoLocalClient.searchByKeyword(keyword, MAX_COUNT);
            log.debug(result.toString());
            return result.getDocuments().stream()
                    .map(KakaoDocumentDto::getPlaceName)
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
