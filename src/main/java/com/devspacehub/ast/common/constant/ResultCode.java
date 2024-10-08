/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ResultCode
 creation : 2024.1.8
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Error code.
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // success
    SUCCESS(2000, "정상 응답."),
    //business logic error
    INSUFFICIENT_MONEY_ERROR(5003, "금액이 충분하지 않습니다."),

    // application error
    DTO_CONVERSION_ERROR(5000, "DTO 변환 과정에서 문제 발생하였습니다."),
    NOT_FOUND_ACCESS_TOKEN(5000, "접근 토큰 조회에 실패하였습니다."),
    NOT_FOUND_RANKING_VOLUME_DATA_JSON_FILE(5004, "거래량 순위 JSON 파일이 존재하지 않습니다."),
    INVALID_OPENAPI_TYPE_ERROR(5005, "해당하는 OpenApiType이 존재하지 않습니다."),
    INVALID_MARKET_TYPE_ERROR(5005, "해당하는 MarketType이 존재하지 않습니다."),
    INVALID_EXCHANGE_CODE_ERROR(5005, "해당하는 ExchangeCode가 존재하지 않습니다."),
    INVALID_CURRENT_PRICE(5005, "유효하지 않은 현재가 입니다."),
    INVALID_VALUE(5005, "유효하지 않은 값 입니다."),
    DATA_IS_NULL_ERROR(5005, "값이 Null 입니다."),
    DATA_IS_BLANK_ERROR(5005, "값이 비었습니다."),

    // KIS server error
    OPENAPI_SERVER_RESPONSE_ERROR(5002, "OpenApi 서버에서 오류 응답을 반환하였습니다."),
    INTERNAL_SERVER_ERROR(5000, "서버 문제 발생하였습니다."),

    // notification error
    NOTIFICATION_ERROR(5003, "알림 요청 중 문제 발생하였습니다.");

    private final int code;
    private final String message;
}
