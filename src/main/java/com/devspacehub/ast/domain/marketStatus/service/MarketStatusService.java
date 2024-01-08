/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MarketStatusService
 creation : 2024.1.4
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.service;

import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 주식 현황 조회 서비스.
 */
@Service
public class MarketStatusService {
    /**
     * 거래량 조회 (1-10위)
     *
     * @return the list
     */
    public List<StockItemDto> findTradingVolume() {


/*        StockItemDto build = StockItemDto.builder()
                .transactionId(txIdBuyOrder)
                .stockCode(stockCode)
                .orderQuantity(orderQuantity)
                .orderPrice(orderPrice)
                .orderDivision(orderDivision)
                .build();
                */

        return null;
    }

}
