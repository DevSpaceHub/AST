/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDto
 creation : 2024.1.4
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceApiResDto;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
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

    @SuperBuilder
    @Getter
    public static class Domestic extends StockItemDto {
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
        public static StockItemDto.Domestic of(StockBalanceApiResDto.MyStockBalance myStockBalance) {
            return StockItemDto.Domestic.builder()
                    .itemCode(myStockBalance.getItemCode())
                    .itemNameKor(myStockBalance.getStockName())
                    .orderQuantity(myStockBalance.getHoldingQuantity())     // 전량 매도
                    .orderPrice(myStockBalance.getCurrentPrice())
                    .orderDivision(ORDER_DIVISION)
                    .build();
        }
    }

    @SuperBuilder
    @Getter
    public static class ReservationStockItem extends StockItemDto {
        private Long reservationSeq;

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
    @SuperBuilder
    @Getter
    public static class Overseas extends StockItemDto {
        private ExchangeCode exchangeCode;

        /**
         * 해외 주식 매수 종목 DTO
         * @param itemCode 종목 코드
         * @param itemNameKor 종목 한글 이름
         * @param orderQuantity 주문 수량
         * @param orderPrice 주문 가
         * @param exchangeCode 거래소 코드
         * @return
         */
        public static Overseas from(String itemCode, String itemNameKor, int orderQuantity, BigDecimal orderPrice, ExchangeCode exchangeCode) {
            return Overseas.builder()
                    .exchangeCode(exchangeCode)
                    .itemCode(itemCode)
                    .itemNameKor(itemNameKor)
                    .orderQuantity(orderQuantity)
                    .orderPrice(orderPrice)
                    .orderDivision(ORDER_DIVISION)
                    .build();
        }

        /**
         * 해외 주식 잔고 조회 응답 DTO 로부터 매도 주문 시 참조할 DTO 생성한다.
         * @param myStockBalance
         * @return 매도 주문 위한 주식 종목 정보 DTO
         */
        public static StockItemDto of(OverseasStockBalanceApiResDto.MyStockBalance myStockBalance) {
            return Overseas.builder()
                    .itemCode(myStockBalance.getItemCode())
                    .itemNameKor(myStockBalance.getStockName())
                    .orderQuantity(myStockBalance.getOrderPossibleQuantity())     // 전량 매도
                    .orderPrice(myStockBalance.getCurrentPrice())
                    .orderDivision(ORDER_DIVISION)
                    .exchangeCode(ExchangeCode.fromCode(myStockBalance.getExchangeCode()))
                    .build();
        }
    }
}
