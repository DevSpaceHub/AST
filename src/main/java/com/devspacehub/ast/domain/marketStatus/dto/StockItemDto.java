/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDto
 creation : 2024.1.4
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.domain.my.dto.response.StockBalanceExternalResDto;
import lombok.*;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;

/**
 * 주식 정보 Dto.
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockItemDto {

    private String stockCode;
    private String stockNameKor;

    private String orderDivision;

    private Integer orderQuantity;

    private Integer orderPrice; // 주문가

    @Builder
    private static StockItemDto StockItemDto(String stockCode, String stockNameKor,
                                             int orderQuantity, int currentStockPrice) {
        return new StockItemDto(stockCode, stockNameKor, ORDER_DIVISION,
                orderQuantity, currentStockPrice);
    }

    /**
     * 매수 종목 DTO 생성
     * @param stockInfo
     * @param orderQuantity
     * @param calculatedBuyPrice
     * @return
     */
    public static StockItemDto from(DomStockTradingVolumeRankingExternalResDto.StockInfo stockInfo, int orderQuantity, int calculatedBuyPrice) {
        return StockItemDto.builder()
                .stockCode(stockInfo.getStockCode())
                .stockNameKor(stockInfo.getHtsStockNameKor())
                .orderQuantity(orderQuantity)
                .currentStockPrice(calculatedBuyPrice)
                .build();
    }

    /**
     * 매도 종목 DTO 생성
     * @param myStockBalance
     * @return
     */
    public static StockItemDto of(StockBalanceExternalResDto.MyStockBalance myStockBalance) {
        return StockItemDto.builder()
                .stockCode(myStockBalance.getStockCode())
                .stockNameKor(myStockBalance.getStockName())
                .orderQuantity(Integer.parseInt(myStockBalance.getHoldingQuantity()))     // 전량 매도
                .currentStockPrice(Integer.parseInt(myStockBalance.getCurrentPrice()))
                .build();
    }
}
