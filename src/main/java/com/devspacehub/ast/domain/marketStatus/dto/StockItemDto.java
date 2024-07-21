/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDto
 creation : 2024.1.4
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.common.constant.DecimalScale;
import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.utils.BigDecimalUtil;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StockItemDto {
    String itemCode;
    String itemNameKor;
    String orderDivision;
    Integer orderQuantity;
    BigDecimal orderPrice;

    @SuperBuilder
    @Getter
    public static class Domestic extends StockItemDto {
        /**
         * 국내 주식 매수 정보 DTO 생성한다.
         * @param stockInfo 주식 종목
         * @param orderQuantity 주문 수량
         * @param orderPrice 주문 단가
         * @return 국내 주식 매수 정보 Dto
         */
        public static StockItemDto buyFrom(DomStockTradingVolumeRankingExternalResDto.StockInfo stockInfo, int orderQuantity, BigDecimal orderPrice) {
            return StockItemDto.builder()
                    .itemCode(stockInfo.getItemCode())
                    .itemNameKor(stockInfo.getHtsStockNameKor())
                    .orderQuantity(orderQuantity)
                    .orderPrice(orderPrice)
                    .orderDivision(ORDER_DIVISION)
                    .build();
        }

        /**
         * 국내 주식 매도 정보 DTO 생성한다.
         * @param myStockBalance 주식잔고조회 응답 Dto 중 일부 필드
         * @return 국내 주식 매도 정보 DTO
         */
        public static StockItemDto.Domestic sellOf(StockBalanceApiResDto.MyStockBalance myStockBalance) {
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
    public static class Overseas extends StockItemDto {
        ExchangeCode exchangeCode;

        /**
         * 해외 주식 매수 종목 DTO
         * @param itemCode 종목 코드
         * @param itemNameKor 종목 한글 이름
         * @param orderQuantity 주문 수량
         * @param orderPrice 주문 가
         * @param exchangeCode 거래소 코드
         * @return 해외 주식 매수 정보 Dto
         */
        public static Overseas from(String itemCode, String itemNameKor, int orderQuantity, BigDecimal orderPrice, ExchangeCode exchangeCode) {
            return new Overseas(itemCode, itemNameKor, ORDER_DIVISION, orderQuantity, orderPrice, exchangeCode);
        }

        /**
         * 해외 주식 잔고 조회 응답 DTO 로부터 매도 주문 시 참조할 DTO 생성한다.<br>
         * @param myStockBalance
         * @return 매도 주문 위한 주식 종목 정보 DTO<br>
         * - orderPrice : 매도 주문 시 소수점 아래 4자리 유지한다.
         */
        public static StockItemDto of(OverseasStockBalanceApiResDto.MyStockBalance myStockBalance) {
            BigDecimal orderPrice = BigDecimalUtil.setScale(myStockBalance.getCurrentPrice(), DecimalScale.FOUR.getCode());
            return new Overseas(myStockBalance.getItemCode(), myStockBalance.getStockName(), ORDER_DIVISION,
                    myStockBalance.getOrderPossibleQuantity(), orderPrice, ExchangeCode.fromCode(myStockBalance.getExchangeCode()));
        }

        /**
         * 생성자
         * @param itemCode 종목 코드
         * @param itemNameKor 종목 한글명
         * @param orderDivision 지정가 필드
         * @param orderQuantity 주문 수량
         * @param orderPrice 주문 가격
         * @param exchangeCode 거래소 코드
         */
        protected Overseas(String itemCode, String itemNameKor, String orderDivision, int orderQuantity, BigDecimal orderPrice, ExchangeCode exchangeCode) {
            super(itemCode, itemNameKor, orderDivision, orderQuantity, orderPrice);
            this.exchangeCode = exchangeCode;
        }
    }
}
