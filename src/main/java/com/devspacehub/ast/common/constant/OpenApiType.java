/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OpenApiType
 creation : 2023.12.17
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The enum Open api OpenApiType.
 */
@Getter
@RequiredArgsConstructor
public enum OpenApiType {
    DOMESTIC_STOCK_BUY_ORDER("/uapi/domestic-stock/v1/trading/order-cash", "국내 주식(현금)-매수"),
    DOMESTIC_STOCK_RESERVATION_BUY_ORDER("/uapi/domestic-stock/v1/trading/order-cash", "국내 주식(현금)-예약 매수"),
    DOMESTIC_STOCK_SELL_ORDER("/uapi/domestic-stock/v1/trading/order-cash", "국내 주식(현금)-매도"),
    BUY_ORDER_POSSIBLE_CASH("/uapi/domestic-stock/v1/trading/inquire-psbl-order", "매수 가능 조회"),
    STOCK_BALANCE("/uapi/domestic-stock/v1/trading/inquire-balance", "주식 잔고 조회"),
    DOMSTOCK_TRADING_VOLUME_RANKING("/uapi/domestic-stock/v1/quotations/volume-rank", "국내 주식 거래량 순위 조회"),
    CURRENT_STOCK_PRICE("/uapi/domestic-stock/v1/quotations/inquire-price", "국내 주식 현재가 시세 조회"),
    OAUTH_ACCESS_TOKEN_ISSUE("/oauth2/tokenP", "접근 토큰 발급"),
    OAUTH_APPROVAL_KEY_ISSUE("/oauth2/Approval", "실시간 (웹소켓) 접속키 발급")
    ;

    private final String uri;
    private final String discription;

}
