/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ApiResult
 creation : 2024.1.12
 author : Yoonji Moon
 */
package com.devspacehub.ast.common.dto;

import com.devspacehub.ast.exception.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ApiResult {
    private Boolean success;
    @Builder.Default
    private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private int status;

    public static ApiResult success() {
        return ApiResult.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .build();
    }

    public static ApiResult failed(ErrorCode errorCode) {
        return ApiResult.builder()
                .success(false)
                .status(errorCode.getStatus().value())
                .build();
    }

}
