/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ApiResult
 creation : 2024.1.12
 author : Yoonji Moon
 */
package com.devspacehub.ast.common.dto;

import com.devspacehub.ast.common.constant.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 컨트롤러 응답 DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ApiResult {
    private Boolean success;
    @Builder.Default
    private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private int code;

    /**
     * Success api result.
     *
     * @return the api result
     */
    public static ApiResult success() {
        return ApiResult.builder()
                .success(true)
                .code(ResultCode.SUCCESS.getCode())
                .build();
    }

    /**
     * Failed api result.
     *
     * @param resultCode the result code
     * @return the api result
     */
    public static ApiResult failed(ResultCode resultCode) {
        return ApiResult.builder()
                .success(false)
                .code(resultCode.getCode())
                .build();
    }

}
