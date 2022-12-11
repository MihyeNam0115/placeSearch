package com.mh.placesearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class KeywordRankingService {

    private static final int topN = 10;
    private final ConcurrentHashMap<String, Long> keywordCountMap;
    private final ReentrantLock reentrantLock;
    private List<KeywordCount> topNKeywordsUnmodifiable;

    public KeywordRankingService() {
        this.keywordCountMap = new ConcurrentHashMap<>();
        this.reentrantLock = new ReentrantLock();
        this.topNKeywordsUnmodifiable = Collections.emptyList();
    }

    public List<KeywordCount> getTopNKeywords() {
        return topNKeywordsUnmodifiable;
    }

    public boolean updateKeywordCountData(String keyword) {
        try {
            if (reentrantLock.tryLock(10, TimeUnit.SECONDS) == false) {
                return false;
            }

            Long count = increaseKeywordCountMap(keyword);
            updateTopNKeywords(keyword, count);
        } catch (InterruptedException e) {
            log.error(e.toString());
            return false;
        } finally {
            reentrantLock.unlock();
        }

        return true;
    }

    private Long increaseKeywordCountMap(String keyword) {
        return keywordCountMap.compute(keyword, (k, v) -> (v == null) ? 1L : v + 1L);
    }

    private void updateTopNKeywords(String keyword, Long count) {
        int sizeOfTopNKeywordDB = topNKeywordsUnmodifiable.size();
        KeywordCount newKeywordCount = KeywordCount.builder().keyword(keyword).count(count).build();

        List<KeywordCount> newTopNKeywords = null;

        if (sizeOfTopNKeywordDB > 0) {
            long minCountValueInTopN = topNKeywordsUnmodifiable.get(0).getCount();
            if (sizeOfTopNKeywordDB > topN && minCountValueInTopN > count) {
                // No Update
                return;
            }
        }

        newTopNKeywords = new ArrayList<>(this.topNKeywordsUnmodifiable);
        newTopNKeywords.add(newKeywordCount);
        KeywordCount oldKeywordCount = KeywordCount.builder().keyword(keyword).count(count - 1).build();
        newTopNKeywords.remove(oldKeywordCount);

        Collections.sort(newTopNKeywords, new KeywordCountComparator().reversed());
        if (newTopNKeywords.size() > topN) {
            newTopNKeywords.remove(newTopNKeywords.size() - 1);
        }

        this.topNKeywordsUnmodifiable = Collections.unmodifiableList(newTopNKeywords);
    }

    static class KeywordCountComparator implements Comparator<KeywordCount> {

        @Override
        public int compare(KeywordCount o1, KeywordCount o2) {
            int result = o1.getCount().compareTo(o2.getCount());
            if (result == 0) {
                return o1.getKeyword().compareTo(o2.getKeyword());
            }
            return result;
        }
    }
}
