/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTradingServiceFactory
 creation : 2024.1.7
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.orderTrading;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.reservationOrderInfo.service.ReservationBuyOrderServiceImpl;
import com.devspacehub.ast.domain.orderTrading.service.BuyOrderServiceImpl;
import com.devspacehub.ast.domain.orderTrading.service.SellOrderServiceImpl;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.domain.orderTrading.service.overseas.OverseasBuyOrderServiceImpl;
import com.devspacehub.ast.domain.orderTrading.service.overseas.OverseasSellOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * OrderTradingService 구현체를 반환하는 팩토리 클래스
 */
@Component
@RequiredArgsConstructor
public class OrderTradingServiceFactory {
    private final BuyOrderServiceImpl buyOrderService;
    private final SellOrderServiceImpl sellOrderService;
    private final ReservationBuyOrderServiceImpl reservationBuyOrderService;
    private final OverseasBuyOrderServiceImpl overseasBuyOrderService;
    private final OverseasSellOrderServiceImpl overseasSellOrderService;

    public TradingService getServiceImpl(OpenApiType type) {
        return switch (type) {
            case DOMESTIC_STOCK_BUY_ORDER -> buyOrderService;
            case DOMESTIC_STOCK_SELL_ORDER -> sellOrderService;
            case DOMESTIC_STOCK_RESERVATION_BUY_ORDER -> reservationBuyOrderService;
            case OVERSEAS_STOCK_BUY_ORDER -> overseasBuyOrderService;
            case OVERSEAS_STOCK_SELL_ORDER -> overseasSellOrderService;
            default -> throw new IllegalArgumentException("적절하지 않은 OpenApiType 구매 타입 입니다.");
        };
    }
}
