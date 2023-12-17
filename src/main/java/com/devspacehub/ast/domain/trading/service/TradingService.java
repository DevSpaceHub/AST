/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : TradingService
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.trading.service;

/**
 * 주식 주문 서비스 인터페이스.
 */
public interface TradingService {
    /**
     * 주식 주문 (매수).
     */
    void buyOrder();

    /**
     * 주식 주문 (매도).
     */
    void sellOrder();
}
