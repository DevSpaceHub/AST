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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResult> handleBusinessException(BusinessException ex) {
        log.error("handleBusinessException", ex);

        ResultCode resultCode = ex.getResultCode();
        return ResponseEntity.status(resultCode.getCode()).body(ApiResult.failed(resultCode));
    }
}
