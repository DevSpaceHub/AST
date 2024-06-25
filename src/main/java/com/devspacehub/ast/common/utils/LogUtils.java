/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : LogUtils
 creation : 2024.4.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.utils;

import com.devspacehub.ast.common.constant.OpenApiType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 공통 메세지에 대한 로그 Util 클래스
 */
@Slf4j
public class LogUtils {
    /**
     * OpenAPI 요청 응답이 오류 응답일 시 남기는 ERROR 로그 메세지
     * @param message
     * @param messageCode
     */
    public static void openApiFailedResponseMessage(OpenApiType openApiType, String message, String messageCode) {
        log.error("[{}] KIS Error Message : {}, Message Code : {}", openApiType.getDiscription(), message, messageCode);
    }

    /**
     * 거래 주문 전, 주문 금액 부족 시 남기는 ERROR 로그 메시지
     * @param openApiType
     * @param stockNameKor
     * @param myDeposit
     */
    public static void insufficientAmountError(OpenApiType openApiType, String stockNameKor, BigDecimal myDeposit) {
        log.error("[{}] 금액 부족. (종목명: {}, 예수금: {})", openApiType.getDiscription(), stockNameKor, myDeposit);
    }

    /**
     * 거래 주문 성공 시 남기는 INFO 로그 메시지
     * @param openApiType
     * @param stockNameKor
     */
    public static void tradingOrderSuccess(OpenApiType openApiType, String stockNameKor) {
        log.info("[{}] 주문 성공 ({})", openApiType.getDiscription(), stockNameKor);
    }

    /**
     * OpenAPI 요청 실패 시 남기는 ERROR  로그 메시지
     * @param uri
     * @param message
     */
    public static void openApiRequestFailed(String uri, String message) {
        log.error("요청 실패하였습니다.(uri : {})", uri);
        log.error(message);
    }

    public static void notFoundDataError(String dataDescription) {
        log.error("{}가 존재하지 않습니다.", dataDescription);
    }
}
