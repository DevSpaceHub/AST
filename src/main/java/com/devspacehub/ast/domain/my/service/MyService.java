/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.domain.my.dto.response.StockBalanceExternalResDto;

/**
 * 사용자 My 서비스 인터페이스.
 */
public interface MyService {
    /**
     * 매수 가능 금액 조회 (Get)
     *
     * @param stockCode     the stock code
     * @param orderPrice    the order price
     * @param orderDivision the order division
     * @return the buy order possible cash
     */
    int getBuyOrderPossibleCash(String stockCode, Integer orderPrice, String orderDivision);

    /**
     * 주식 잔고 조회 (Get)
     *
     * @return the my stock balance
     */
    StockBalanceExternalResDto getMyStockBalance();
}
