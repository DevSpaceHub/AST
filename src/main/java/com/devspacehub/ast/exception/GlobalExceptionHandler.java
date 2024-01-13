/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : GlobalExceptionHandler
 creation : 2024.1.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception;

import com.devspacehub.ast.common.dto.ApiResult;
import com.devspacehub.ast.exception.error.BusinessException;
import com.devspacehub.ast.exception.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResult> handleBusinessException(BusinessException ex) {
        log.error("handleBusinessException", ex);

        ErrorCode errorCode = ex.getErrorCode();
        ApiResult result = ApiResult.builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatus()).body(result);
    }
}
