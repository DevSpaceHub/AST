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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResult> handleBusinessException(BusinessException ex) {
        log.error("ResultCode : {}, Messeage : {}", ex.getResultCode(), ex.getMessage());

        ResultCode resultCode = ex.getResultCode();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResult.failed(resultCode));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResult> handleException(Exception ex) {
        log.error("handleException", ex);

        ResultCode resultCode = ResultCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failed(resultCode));
    }
}
