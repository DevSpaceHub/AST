/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SplitBuyPercentsTest
 creation : 2024.2.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 매수 분할 퍼센트 클래스 단위 테스트
 */
class SplitBuyPercentsTest {

    @Test
    @DisplayName("현재가에 대해 분할 매수 퍼센트에 따른 구매 단가를 계산한다.")
    void calculateBuyPriceBySplitBuyPercents() {
        // given
        String percentsStr = "2,5,8";
        final int currentPrice = 99900;
        SplitBuyPercents splitBuyPercents = SplitBuyPercents.of(percentsStr);

        // when & then
        assertThat(splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 0)).isEqualTo((currentPrice - (int)(currentPrice * 0.02f)));
        assertThat(splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 1)).isEqualTo(currentPrice - (int)(currentPrice * 0.05f));
        assertThat(splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 2)).isEqualTo(currentPrice - (int)(currentPrice * 0.08f));
    }
}