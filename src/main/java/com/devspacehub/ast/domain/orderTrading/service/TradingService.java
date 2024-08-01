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
import com.devspacehub.ast.domain.my.service.MyServiceFactory;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;

/**
 * 주식 주문 서비스 인터페이스.
 */
@RequiredArgsConstructor
public abstract class TradingService {
    protected final OpenApiRequest openApiRequest;
    protected final Notificator notificator;
    protected final OrderTradingRepository orderTradingRepository;
    protected final MyServiceFactory myServiceFactory;
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
     * OpenAPI 매수/매도 주문 요청의 응답에 대한 결과 처리<br>
     * - 성공 -> 메세지 발송<br>
     * - 실패 -> 에러 로그
     * @param result 매수/매도 주문 응답 Dto
     * @param orderTrading 종목 정보 Dto
     */
    public abstract <T extends WebClientCommonResDto> void orderApiResultProcess(T result, OrderTrading orderTrading);


    /**
     * 매수 가능한지 체크한다.
     * @param itemCode 주식 종목 코드
     * @param transactionId 트랜잭션 Id
     * @return 유효한 종목이고 신규 주문이면 True 반환한다. 반대는 False.
     */
    protected boolean isStockItemBuyOrderable(String itemCode, String transactionId, LocalDateTime marketStart, LocalDateTime marketEnd) {
        return isNewOrder(itemCode, transactionId, marketStart, marketEnd);
    }

    /**
     * 종목에 대해 새 주문인지 체크
     * @param itemCode     String 타입의 종목 코드
     * @param transactionId 트랜잭션 Id
     * @param marketStart 장 시작 시각
     * @param marketEnd 장 종료 시각
     * @return 금일 기준 신규 주문이면 True 반환. 이미 주문 이력 있으면 False 반환.
     */
    protected boolean isNewOrder(String itemCode, String transactionId, LocalDateTime marketStart, LocalDateTime marketEnd){
        return 0 == orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                itemCode, OPENAPI_SUCCESS_RESULT_CODE, transactionId, marketStart, marketEnd);
    }
}
