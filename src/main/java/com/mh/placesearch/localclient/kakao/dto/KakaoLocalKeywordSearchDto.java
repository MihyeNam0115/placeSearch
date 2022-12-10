package com.mh.placesearch.localclient.kakao.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Value
public class KakaoLocalKeywordSearchDto {
    private KakaoMetaDto meta;
    private List<KakaoDocumentDto> documents;
}
