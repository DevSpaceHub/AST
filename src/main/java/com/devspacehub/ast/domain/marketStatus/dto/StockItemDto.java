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
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;

/**
 * 주식 정보 Dto.
 */
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockItemDto {

    private String itemCode;
    private String itemNameKor;

    private String orderDivision;

    private Integer orderQuantity;

    private BigDecimal orderPrice;

    /**
     * 매수 종목 DTO 생성
     * @param stockInfo
     * @param orderQuantity
     * @param orderPrice
     * @return
     */
    public static StockItemDto from(DomStockTradingVolumeRankingExternalResDto.StockInfo stockInfo, int orderQuantity, BigDecimal orderPrice) {
        return StockItemDto.builder()
                .itemCode(stockInfo.getItemCode())
                .itemNameKor(stockInfo.getHtsStockNameKor())
                .orderQuantity(orderQuantity)
                .orderPrice(orderPrice)
                .orderDivision(ORDER_DIVISION)
                .build();
    }

    /**
     * 매도 종목 DTO 생성
     * @param myStockBalance
     * @return
     */
    public static StockItemDto of(StockBalanceExternalResDto.MyStockBalance myStockBalance) {
        return StockItemDto.builder()
                .itemCode(myStockBalance.getItemCode())
                .itemNameKor(myStockBalance.getStockName())
                .orderQuantity(Integer.parseInt(myStockBalance.getHoldingQuantity()))     // 전량 매도
                .orderPrice(new BigDecimal(myStockBalance.getCurrentPrice()))
                .orderDivision(ORDER_DIVISION)
                .build();
    }
    @SuperBuilder
    @Getter
    public static class ReservationStockItem extends StockItemDto {
        private  Long reservationSeq;

        /**
         * 예약 매수 종목 DTO
         * @param reservationOrderInfo
         * @return
         */
        public static ReservationStockItem of(ReservationOrderInfo reservationOrderInfo) {
            return ReservationStockItem.builder()
                    .reservationSeq(reservationOrderInfo.getSeq())
                    .itemCode(reservationOrderInfo.getItemCode())
                    .itemNameKor(reservationOrderInfo.getKoreanItemName())
                    .orderQuantity(reservationOrderInfo.getOrderQuantity())
                    .orderPrice(reservationOrderInfo.getOrderPrice())
                    .orderDivision(ORDER_DIVISION)
                    .build();
        }
    }
}
