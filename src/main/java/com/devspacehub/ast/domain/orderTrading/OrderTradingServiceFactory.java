/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderTradingServiceFactory
 creation : 2024.1.7
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.orderTrading;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.orderTrading.service.BuyOrderServiceImpl;
import com.devspacehub.ast.domain.orderTrading.service.SellOrderServiceImpl;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderTradingServiceFactory {
    private final BuyOrderServiceImpl buyOrderService;
    private final SellOrderServiceImpl sellOrderService;

    public TradingService getServiceImpl(OpenApiType type) {
        switch (type) {
            case DOMESTIC_STOCK_BUY_ORDER -> {
                return buyOrderService;
            }
            case DOMESTIC_STOCK_SELL_ORDER -> {
                return sellOrderService;
            }
            default -> throw new IllegalArgumentException("적절하지 않은 구매 타입입니다.");
        }
    }
}
