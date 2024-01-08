/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCheckExternalReqDto
 creation : 2023.12.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto.request;

import com.devspacehub.ast.common.dto.WebClientRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * 매수 가능 조회 요청 DTO.
 */
@Builder
public class BuyPossibleCheckExternalReqDto extends WebClientRequestDto {

    /**
     * Sets headers.
     *
     * @param oauth the oauth
     * @param txId  the tx id
     * @return the headers
     */
    @JsonIgnore
    public static Consumer<HttpHeaders> setHeaders(String oauth, String txId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        headers.add("authorization", "Bearer " + oauth);
        headers.add("tr_id", txId);
        return httpHeaders -> httpHeaders.addAll(headers);
    }
}
