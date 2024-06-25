/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : TradingService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주식 주문 서비스 인터페이스.
 */
public abstract class TradingService {
    /**
     * 주식 주문 (매수/매도).
     */
    public abstract List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType);

    /**
     * 주문 API 요청 위해 Header, Body 세팅 후 OpenApiRequest 클래스 내 메서드 호출.
     * @param openApiProperties
     * @param stockItem
     * @param openApiType
     * @param transactionId
     * @return
     */
    public abstract <T extends StockItemDto> WebClientCommonResDto callOrderApi(OpenApiProperties openApiProperties, T stockItem, OpenApiType openApiType, String transactionId);
    /**
     * 주식 주문 정보 저장.
     */
    public abstract List<OrderTrading> saveOrderInfos(List<OrderTrading> orderTradingInfos);

    /**
     * 금일 한번도 매수/매도 주문되지 않은 종목인지 체크.
     * @param itemCode 주식 종목 코드
     * @param transactionId API 별 트랜잭션 ID
     * @param marketStart 장 시작 시각
     * @param marketEnd 장 종료 시각
     * @return
     */
    public boolean isNewOrder(String itemCode, String transactionId, LocalDateTime marketStart, LocalDateTime marketEnd) {
        return true;
    }

    /**
     * OpenAPI 매수/매도 주문 요청의 응답에 대한 결과 처리<br>
     * - 성공 -> 메세지 발송<br>
     * - 실패 -> 에러 로그
     * @param result 매수/매도 주문 응답 Dto
     * @param orderTrading 종목 정보 Dto
     */
    public abstract <T extends WebClientCommonResDto> void orderApiResultProcess(T result, OrderTrading orderTrading);

}
