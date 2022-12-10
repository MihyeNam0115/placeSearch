package com.mh.placesearch.localclient.kakao;

import com.mh.placesearch.localclient.kakao.dto.KakaoLocalKeywordSearchDto;
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
class KakaoLocalClientTest {

    @Autowired
    private KakaoLocalClient kakaoLocalClient;

    @Test
    void searchAddress_whenSearchValidKeyword_shouldReturnUnder5Items() {
        KakaoLocalKeywordSearchDto result = kakaoLocalClient.searchByKeyword("카카오프렌즈", 5);
        assertThat(result.getDocuments()).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    void searchAddress_whenSearchValidKeyword2_shouldReturnUnder5Items() {
        KakaoLocalKeywordSearchDto result = kakaoLocalClient.searchByKeyword("곱창", 5);
        assertThat(result.getDocuments()).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    void searchAddress_whenInValidKeyword_shouldReturn0() {
        KakaoLocalKeywordSearchDto result = kakaoLocalClient.searchByKeyword("Invalid Keyword", 5);
        assertThat(result.getDocuments()).hasSize(0);
    }

    @Test
    void searchAddress_whenEmptyKeyword_shouldThrowBadRequestException() {
        assertThatThrownBy(() -> kakaoLocalClient.searchByKeyword("", 5))
                .isInstanceOf(FeignException.BadRequest.class)
                .hasMessageContaining("MissingParameter");
    }

    @Test
    void searchAddress_whenOnlySpaceKeyword_shouldReturn0() {
        KakaoLocalKeywordSearchDto result = kakaoLocalClient.searchByKeyword(" ", 5);
        assertThat(result.getDocuments()).hasSize(0);
    }
}
