/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : WebClientCommonReqDto
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * OpenApi 호출 시 사용하는 공통 Request DTO.
 */
@NoArgsConstructor
@Getter
public abstract class WebClientCommonReqDto {

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
