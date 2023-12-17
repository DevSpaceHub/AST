/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiCommonDto
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * OpenApi 호출 - 공통 Request DTO
 */
// header에 해당하므로, api 콜하는 메서드에서 바로 땡겨도 될듯??
@NoArgsConstructor
@Getter
public abstract class WebClientRequestDto {

    /**
     * Gets body.
     *
     * @param <T> the type parameter
     * @return the body
     */
    public abstract <T extends WebClientRequestDto> T getBody();

//    public abstract Consumer<HttpHeaders> setHeaders();
}
