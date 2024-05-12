/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.domain.my.stockBalance.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;

import java.time.LocalDate;
import java.util.List;

/**
 * 사용자 My 서비스 인터페이스.
 */
public interface MyService {
    /**
     * 매수 가능 금액 조회 (Get)
     *
     * @param itemCode     the stock code
     * @param orderPrice    the order price
     * @param orderDivision the order division
     * @return the buy order possible cash
     */
    int getBuyOrderPossibleCash(String itemCode, Integer orderPrice, String orderDivision);

    /**
     * 주식 잔고 조회 (Get)
     *
     * @return 나의 주식 잔고
     */
    StockBalanceExternalResDto getMyStockBalance();

    boolean isMyDepositLowerThanOrderPrice(int myDeposit, int orderPrice);

    /**
     * 금일 체결된 종목 조회 (Get)
     */
    List<OrderConclusionDto> getConcludedStock(LocalDate today);

    /**
     * 예약 매수 사용 여부 업데이트
     */
    void updateMyReservationOrderUseYn(OrderConclusionDto orderConclusion, LocalDate concludedDate);
}
