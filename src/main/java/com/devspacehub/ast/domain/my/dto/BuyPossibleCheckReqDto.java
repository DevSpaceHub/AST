/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyPossibleCheckReqDto
 creation : 2023.12.17
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.dto;

import com.devspacehub.ast.common.dto.WebClientRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;
@Builder
public class BuyPossibleCheckReqDto extends WebClientRequestDto {


    @JsonIgnore
    public static Consumer<HttpHeaders> setHeaders(String oauth) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-Type", "application/json");
        headers.add("authorization", "Bearer " + oauth);
        return httpHeaders -> {
            httpHeaders.addAll(headers);
        };
    }
}
