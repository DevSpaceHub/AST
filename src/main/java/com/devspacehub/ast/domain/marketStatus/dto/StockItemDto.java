/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDto
 creation : 2024.1.4
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceExternalResDto;
import lombok.*;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;

/**
 * 주식 정보 Dto.
 */
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockItemDto {

    private String itemCode;
    private String itemNameKor;

    private String orderDivision;

    private Integer orderQuantity;

    private Integer orderPrice;

    @Builder
    private static StockItemDto stockItemDto(String stockCode, String stockNameKor,
                                             int orderQuantity, int orderPrice) {
        return new StockItemDto(stockCode, stockNameKor, ORDER_DIVISION,
                orderQuantity, orderPrice);
    }

    /**
     * 매수 종목 DTO 생성
     * @param stockInfo
     * @param orderQuantity
     * @param orderPrice
     * @return
     */
    public static StockItemDto from(DomStockTradingVolumeRankingExternalResDto.StockInfo stockInfo, int orderQuantity, int orderPrice) {
        return StockItemDto.builder()
                .stockCode(stockInfo.getItemCode())
                .stockNameKor(stockInfo.getHtsStockNameKor())
                .orderQuantity(orderQuantity)
                .orderPrice(orderPrice)
                .build();
    }

    /**
     * 매도 종목 DTO 생성
     * @param myStockBalance
     * @return
     */
    public static StockItemDto of(StockBalanceExternalResDto.MyStockBalance myStockBalance) {
        return StockItemDto.builder()
                .stockCode(myStockBalance.getItemCode())
                .stockNameKor(myStockBalance.getStockName())
                .orderQuantity(Integer.parseInt(myStockBalance.getHoldingQuantity()))     // 전량 매도
                .orderPrice(Integer.parseInt(myStockBalance.getCurrentPrice()))
                .build();
    }

    /**
     * 예약 매수 종목 DTO
     * @param reservationOrderInfo
     * @return
     */
    public static StockItemDto of(ReservationOrderInfo reservationOrderInfo) {
        return StockItemDto.builder()
                .stockCode(reservationOrderInfo.getItemCode())
                .stockNameKor(reservationOrderInfo.getKoreanItemName())
                .orderQuantity(reservationOrderInfo.getOrderQuantity())
                .orderPrice(reservationOrderInfo.getOrderPrice())
                .build();
    }
}
