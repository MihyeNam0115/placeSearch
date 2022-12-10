package com.mh.placesearch.localclient.naver;


import com.mh.placesearch.localclient.naver.dto.NaverLocalKeywordSearchDto;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        properties = {
                "feign.client.config.default.loggerLevel=FULL",
                "logging.level.com.mh.placesearch.localclient: DEBUG"
        })
class NaverLocalClientTest {

    @Autowired
    private NaverLocalClient naverLocalClient;

    @Test
    void searchAddress_whenSearchValidKeyword_shouldReturnUnder5Items() {
        NaverLocalKeywordSearchDto result = naverLocalClient.searchByKeyword("카카오프렌즈", 5);
        assertThat(result.getItems()).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    void searchAddress_whenSearchValidKeyword2_shouldReturnUnder5Items() {
        NaverLocalKeywordSearchDto result = naverLocalClient.searchByKeyword("곱창", 5);
        assertThat(result.getItems()).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    void searchAddress_whenInValidKeyword_shouldReturn0() {
        NaverLocalKeywordSearchDto result = naverLocalClient.searchByKeyword("Invalid Keyword", 5);
        assertThat(result.getItems()).hasSize(0);
    }

    @Test
    void searchAddress_whenEmptyKeyword_shouldThrowBadRequestException() {
        assertThatThrownBy(() -> naverLocalClient.searchByKeyword("", 5))
                .isInstanceOf(FeignException.BadRequest.class)
                .hasMessageContaining("SE01");
    }

    @Test
    void searchAddress_whenOnlySpaceKeyword_shouldThrowBadRequestException() {
        assertThatThrownBy(() -> naverLocalClient.searchByKeyword(" ", 5))
                .isInstanceOf(FeignException.BadRequest.class)
                .hasMessageContaining("SE01");
    }
}
