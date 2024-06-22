/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiType
 creation : 2023.12.17
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import com.devspacehub.ast.exception.error.InvalidValueException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * The enum Open api OpenApiType.
 */
@Getter
@RequiredArgsConstructor
public enum OpenApiType {
    DOMESTIC_STOCK_BUY_ORDER("/uapi/domestic-stock/v1/trading/order-cash", "DM001", "국내 주식(현금)-매수"),
    DOMESTIC_STOCK_SELL_ORDER("/uapi/domestic-stock/v1/trading/order-cash", "DM002", "국내 주식(현금)-매도"),
    DOMESTIC_STOCK_RESERVATION_BUY_ORDER("/uapi/domestic-stock/v1/trading/order-cash", "DM003", "국내 주식(현금)-예약 매수"),
    DOMESTIC_BUY_ORDER_POSSIBLE_CASH("/uapi/domestic-stock/v1/trading/inquire-psbl-order", "DM004", "국내 매수 가능 금액 조회"),
    DOMESTIC_STOCK_BALANCE("/uapi/domestic-stock/v1/trading/inquire-balance", "DM005", "국내 주식 잔고 조회"),
    DOMESTIC_TRADING_VOLUME_RANKING("/uapi/domestic-stock/v1/quotations/volume-rank", "DM006", "국내 주식 거래량 순위 조회"),
    CURRENT_STOCK_PRICE("/uapi/domestic-stock/v1/quotations/inquire-price", "DM007", "국내 주식 현재가 시세 조회"),
    DOMESTIC_ORDER_CONCLUSION_FIND("/uapi/domestic-stock/v1/trading/inquire-daily-ccld", "DM008", "국내 주식 일별주문 체결 조회"),
    OAUTH_ACCESS_TOKEN_ISSUE("/oauth2/tokenP", "CM001", "접근 토큰 발급"),
    OAUTH_APPROVAL_KEY_ISSUE("/oauth2/Approval", "CM002", "실시간 (웹소켓) 접속키 발급"),

    OVERSEAS_STOCK_BUY_ORDER("/uapi/overseas-stock/v1/trading/order", "OV001", "해외 주식(현금)-매수"),
    OVERSEAS_STOCK_SELL_ORDER("/uapi/overseas-stock/v1/tradin경g/order", "OV002", "해외 주식(현금)-매도"),
    OVERSEAS_STOCK_RESERVATION_BUY_ORDER("/uapi/overseas-stock/v1/trading/order", "OV003", "해외 주식(현금)-예약 매수"),

    OVERSEAS_BUY_ORDER_POSSIBLE_CASH("/uapi/overseas-stock/v1/trading/inquire-psamount", "OV004", "해외 매수 가능 금액 조회"),
    OVERSEAS_STOCK_BALANCE("/uapi/overseas-stock/v1/trading/inquire-balance", "OV005", "해외 주식 잔고 조회"),
    OVERSEAS_STOCK_CONDITION_SEARCH("/uapi/overseas-price/v1/quotations/inquire-search", "OV006", "해외주식 조건검색")
    ;

    private final String uri;
    private final String code;
    private final String discription;

    /**
     * 인자로 받은 String 타입의 code 값이 OpenApiType 중 일치하는 타입을 반환한다.
     * @param code String 타입 code 필드
     * @return code 필드 값이 일치하는 상수
     */
    public static OpenApiType fromCode(String code) {
        return Arrays.stream(OpenApiType.values())
                .filter(type -> type.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ResultCode.INVALID_OPENAPI_TYPE_ERROR));
    }
}
