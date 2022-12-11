package com.mh.placesearch.service;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordRankingServiceTest {

    @Test
    void updateKeywordCountData() {
    }

    @Test
    void updateKeywordCountData_whenAdd3Times_shouldSucessWithCount3() {

        KeywordRankingService keywordRankingService = new KeywordRankingService();
        keywordRankingService.updateKeywordCountData("ABC");
        keywordRankingService.updateKeywordCountData("ABC");
        keywordRankingService.updateKeywordCountData("ABC");

        List<KeywordCount> results = keywordRankingService.getTopNKeywords();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getKeyword()).isEqualTo("ABC");
        assertThat(results.get(0).getCount()).isEqualTo(3);
    }

    @Test
    void updateKeywordCountData_whenAdd3Items_shouldSuccess() {

        KeywordRankingService keywordRankingService = new KeywordRankingService();
        keywordRankingService.updateKeywordCountData("ABC");
        keywordRankingService.updateKeywordCountData("CDE");
        keywordRankingService.updateKeywordCountData("AAA");

        List<KeywordCount> results = keywordRankingService.getTopNKeywords();
        assertThat(results).hasSize(3);
        assertThat(results).extracting(KeywordCount::getKeyword)
                .containsExactlyInAnyOrder("ABC", "CDE", "AAA");
        assertThat(results).extracting(KeywordCount::getCount)
                .containsExactlyInAnyOrder(1L, 1L, 1L);
    }

    @Test
    void updateKeywordCountData_whenOver10Items_shouldReturnOnly10() {

        KeywordRankingService keywordRankingService = new KeywordRankingService();
        for (int i = 0; i < 20; i++) {
            keywordRankingService.updateKeywordCountData("ABC" + i);
        }

        List<KeywordCount> results = keywordRankingService.getTopNKeywords();
        assertThat(results).hasSize(10);
    }

    @Test
    void updateKeywordCountData_whenOver10ItemsWithDiffCount_shouldReturnOnly10() {

        KeywordRankingService keywordRankingService = new KeywordRankingService();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                keywordRankingService.updateKeywordCountData("ABC" + i);
            }
        }

        List<KeywordCount> results = keywordRankingService.getTopNKeywords();
        assertThat(results).hasSize(10);
        assertThat(results).extracting(KeywordCount::getKeyword)
                .containsExactly("ABC19", "ABC18", "ABC17", "ABC16", "ABC15", "ABC14", "ABC13", "ABC12", "ABC11", "ABC10");
        assertThat(results).extracting(KeywordCount::getCount)
                .containsExactly(19L, 18L, 17L, 16L, 15L, 14L, 13L, 12L, 11L, 10L);
    }

    @Test
    void updateKeywordCountData_whenReplaceWithHigherValue_shouldReplacedValueReturn() {

        KeywordRankingService keywordRankingService = new KeywordRankingService();
        for (int i = 10; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                keywordRankingService.updateKeywordCountData("ABC" + i);
            }
        }

        // Make ABC15 as 21.
        keywordRankingService.updateKeywordCountData("ABC" + 15);
        keywordRankingService.updateKeywordCountData("ABC" + 15);
        keywordRankingService.updateKeywordCountData("ABC" + 15);
        keywordRankingService.updateKeywordCountData("ABC" + 15);
        keywordRankingService.updateKeywordCountData("ABC" + 15);
        keywordRankingService.updateKeywordCountData("ABC" + 15);

        List<KeywordCount> results = keywordRankingService.getTopNKeywords();
        assertThat(results).hasSize(10);
        assertThat(results).extracting(KeywordCount::getKeyword)
                .containsExactly("ABC15", "ABC19", "ABC18", "ABC17", "ABC16", "ABC14", "ABC13", "ABC12", "ABC11", "ABC10");
        assertThat(results).extracting(KeywordCount::getCount)
                .containsExactly(21L, 19L, 18L, 17L, 16L, 14L, 13L, 12L, 11L, 10L);
    }

    @Test
    void updateKeywordCountData_whenMultiThreadWithOneItem_shouldSuccess() throws InterruptedException {
        int numberOfThreads = 20;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * 2);
        KeywordRankingService keywordRankingService = new KeywordRankingService();
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                System.out.println("Start");
                try {
                    Thread.sleep(RandomUtils.nextInt(100, 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("updateKeywordCountData");
                keywordRankingService.updateKeywordCountData("ABC" + 15);
                System.out.println("End");
                latch.countDown();
            });
        }
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                System.out.println("Start");
                try {
                    Thread.sleep(RandomUtils.nextInt(100, 900));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("getTopNKeywords");
                List<KeywordCount> results = keywordRankingService.getTopNKeywords();
                System.out.println("End" + results.toString());
                latch.countDown();
            });
        }

        latch.await();
        List<KeywordCount> results = keywordRankingService.getTopNKeywords();
        assertThat(results).hasSize(1);
        assertThat(results).extracting(KeywordCount::getKeyword)
                .containsExactly("ABC15");
        assertThat(results).extracting(KeywordCount::getCount)
                .containsExactly((long) numberOfThreads);
    }


    @Test
    void updateKeywordCountData_whenMultiThreadWithMultipleItem_shouldSuccess() throws InterruptedException {
        int numberOfKeywords = 20;
        ExecutorService service = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(230);
        KeywordRankingService keywordRankingService = new KeywordRankingService();
        for (int i = 0; i < numberOfKeywords; i++) {
            for (int j = 0; j <= i; j++) {
                int postfixName = i;
                service.submit(() -> {
                    System.out.println("Start");
                    try {
                        Thread.sleep(RandomUtils.nextInt(100, 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("updateKeywordCountData");
                    keywordRankingService.updateKeywordCountData("ABC" + postfixName);
                    System.out.println("End");
                    latch.countDown();
                });
            }
        }
        for (int i = 0; i < numberOfKeywords; i++) {
            service.submit(() -> {
                System.out.println("Start");
                try {
                    Thread.sleep(RandomUtils.nextInt(100, 900));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("getTopNKeywords");
                List<KeywordCount> results = keywordRankingService.getTopNKeywords();
                System.out.println("End" + results.toString());
                latch.countDown();
            });
        }

        latch.await();
        List<KeywordCount> results = keywordRankingService.getTopNKeywords();
        assertThat(results).hasSize(10);
        assertThat(results).extracting(KeywordCount::getKeyword)
                .containsExactly("ABC19", "ABC18", "ABC17", "ABC16", "ABC15", "ABC14", "ABC13", "ABC12", "ABC11", "ABC10");
        assertThat(results).extracting(KeywordCount::getCount)
                .containsExactly(20L, 19L, 18L, 17L, 16L, 15L, 14L, 13L, 12L, 11L);
    }
}