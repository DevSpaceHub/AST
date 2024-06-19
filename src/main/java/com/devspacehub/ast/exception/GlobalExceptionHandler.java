/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : GlobalExceptionHandler
 creation : 2024.1.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception;

import com.devspacehub.ast.common.dto.ApiResult;
import com.devspacehub.ast.exception.error.BusinessException;
import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.exception.error.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역에서 발생하는 Exception을 처리한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResult> handleBusinessException(BusinessException ex) {
        log.error("ResultCode : {}, Messeage : {}", ex.getResultCode(), ex.getMessage());

        ResultCode resultCode = ex.getResultCode();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResult.failed(resultCode));
    }

    /**
     * 고려하지 못한 Exception 발생 시 처리한다.
     * @param ex Exception 타입 에러
     * @return 500 상태코드 + 실패 ApiResult Dto 담은 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResult> handleException(Exception ex) {
        log.error("handleException", ex);

        ResultCode resultCode = ResultCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failed(resultCode));
    }

    /**
     * InternalServerErrorException 문제 발생 시 처리한다.
     * @param ex InternalServerErrorException을 상속받는 모든 Exception 클래스
     * @return 500 상태코드 + 실패 ApiResult Dto 담은 ResponseEntity
     */
    @ExceptionHandler(InternalServerErrorException.class)
    protected ResponseEntity<ApiResult> handleException(InternalServerErrorException ex) {
        log.error("handleException", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failed(ex.getResultCode()));
    }
}
