/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationStockItem
 creation : 2024.7.4
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.math.BigDecimal;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;

/**
 * 예약 종목 정보 Dto
 */
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReservationStockItem {
    StockItemDto stockItem;
    Long reservationSeq;

    /**
     * Getter
     * @return reservationSeq
     */
    public Long getSeq() {
        return this.reservationSeq;
    }

    /**
     * Getter
     * @return itemCode
     */
    public String getItemCode() {
        return this.stockItem.getItemCode();
    }

    /**
     * Getter
     * @return orderQuantity
     */
    public int getOrderQuantity() {
        return this.stockItem.getOrderQuantity();
    }

    /**
     * Getter
     * @return orderPrice
     */
    public BigDecimal getOrderPrice() {
        return this.stockItem.getOrderPrice();
    }

    /**
     * Getter
     * @return itemNameKor
     */
    public String getItemNameKor() {
        return this.stockItem.getItemNameKor();
    }

    /**
     * Getter<br>
     * stockItem의 타입이 StockItem.Overseas일 때만 유효하다.
     * @return exchangeCode
     */
    public ExchangeCode getExchangeCode() {
        if (this.stockItem instanceof StockItemDto.Overseas) {
            return ((StockItemDto.Overseas) this.stockItem).getExchangeCode();
        }
        return null;
    }

    /**
     * 예약 매수 종목 DTO 생성한다.
     *
     * @param reservationOrderInfo 예약 매수 정보 Entity
     * @return 국내 예약매수 정보 Dto
     */
    public static ReservationStockItem ofDomestic(ReservationOrderInfo reservationOrderInfo) {
        return ReservationStockItem.builder()
                .reservationSeq(reservationOrderInfo.getSeq())
                .stockItem(StockItemDto.builder()
                        .itemCode(reservationOrderInfo.getItemCode())
                        .itemNameKor(reservationOrderInfo.getKoreanItemName())
                        .orderQuantity(reservationOrderInfo.getOrderQuantity())
                        .orderPrice(reservationOrderInfo.getOrderPrice())
                        .orderDivision(ORDER_DIVISION)
                        .build())
                .build();
    }


    /**
     * 해외 주식 종목 위한 생성자.<br>
     * QueryDsl 반환 객체로써 사용되기 위함.
     * @param itemCode 종목 코드
     * @param itemNameKor 종목명
     * @param orderQuantity 주문 수량
     * @param orderPrice 주문가
     * @param exchangeCode 거래소 코드
     */
    @QueryProjection
    public ReservationStockItem(Long reservationSeq, String itemCode, String itemNameKor, Integer orderQuantity,
                    BigDecimal orderPrice, String exchangeCode) {
        this.stockItem = StockItemDto.Overseas.builder()
                .itemCode(itemCode)
                .itemNameKor(itemNameKor)
                .orderDivision(ORDER_DIVISION)
                .orderQuantity(orderQuantity)
                .orderPrice(orderPrice)
                .exchangeCode(ExchangeCode.fromCode(exchangeCode))
                .build();
        this.reservationSeq = reservationSeq;
    }
}
