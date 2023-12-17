/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : mashup
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.domain.trading.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The type Mashup service.
 */
@Service
@RequiredArgsConstructor
public class MashupServiceImpl {
    private final TradingService tradingService;

    /**
     * 거래 시작 메서드
     */
    public void startTrading() {
        // 1. 예수금 조회 (MyService)

        // 주식 주문 가능 여부 체크 (매수가능조회 api)
        /*if (isBuyPossible()) {

        }
*/
        // 2. 주식 주문 (TradingService)
        // 필요한 dto 생성??
        tradingService.buyOrder();
    }
}
