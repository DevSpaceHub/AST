/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MyService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

/**
 * 사용자 My 서비스 인터페이스.
 */
public interface MyService {
    /**
     * 매수 가능 금액 조회 (Get)
     */
    int getBuyOrderPossibleCash(String stockCode, Integer orderPrice, String orderDivision);

    boolean BuyOrderPossibleCheck(String stockCode, String orderDivision, Integer orderPrice);
}
