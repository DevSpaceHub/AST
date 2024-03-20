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
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;

import java.util.List;

/**
 * 주식 주문 서비스 인터페이스.
 */
public abstract class TradingService {
    /**
     * 알고리즘에 따라 거래할 종목 선택
     */
    public abstract <T extends WebClientCommonResDto> List<StockItemDto> pickStockItems(T stockItems, String transactionId);
    /**
     * 주식 주문 (매수/매도).
     */
    public abstract DomesticStockOrderExternalResDto order(OpenApiProperties openApiProperties, StockItemDto stockItemDto, OpenApiType openApiType, String transactionId);

    /**
     * 주식 주문 정보 저장.
     */
    public abstract void saveInfos(List<OrderTrading> orderTradingInfos);

    /**
     * 금일 한번도 매수/매도 주문되지 않은 종목인지 체크.
     * @param stockCode
     * @param transactionId
     * @return
     */
    public abstract boolean isNewOrder(String stockCode, String transactionId);

}
