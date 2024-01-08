/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : TradingService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;

import java.util.List;

/**
 * 주식 주문 서비스 인터페이스.
 */
public abstract class TradingService {
    /**
     * 주식 주문 (매수/매도).
     */
    public abstract DomesticStockOrderExternalResDto order(StockItemDto stockItemDto);

    /**
     * 주식 주문 정보 저장.
     */
    public abstract void saveInfos(List<OrderTrading> orderTradingInfos);
}
