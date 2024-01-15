/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ErrorCode
 creation : 2024.1.9
 author : Yoonji Moon
 */

/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ErrorCode
 creation : 2024.1.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * The enum Error code.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    //business logic error
    NOT_ENOUGH_CASH_ERROR(HttpStatus.NOT_ACCEPTABLE, "NOT_ENOUGH_CASH_ERROR", "금액이 충분하지 않습니다."),


    // application error
    DTO_CONVERSION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "DTO_CONVERSION_ERROR", "DTO 변환 과정에서 문제 발생하였습니다."),
    OPENAPI_FAILED_RESPONSE(HttpStatus.CONFLICT, "OPENAPI_FAILED_RESPONSE", "OpenApi 요청이 실패하였습니다.");

    private HttpStatus status;
    private String code;
    private String message;
}
