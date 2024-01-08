/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ErrorCode
 creation : 2024.1.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * The enum Error code.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_ENOUGH_CASH_ERROR(HttpStatus.NOT_ACCEPTABLE, "NOT_ENOUGH_CASH_ERROR", "금액이 충분하지 않습니다.")
    ;

    private HttpStatus status;
    private String errorCode;
    private String message;
}
