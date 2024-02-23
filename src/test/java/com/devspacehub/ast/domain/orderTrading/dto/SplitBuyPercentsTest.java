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
        final Float twoPercent = 0.02F;
        final Float fivePercent = 0.05F;
        final Float eightPercent = 0.08F;
        final int currentPrice = 99900;
        Float[] percents = new Float[]{twoPercent, fivePercent, eightPercent};
        SplitBuyPercents splitBuyPercents = new SplitBuyPercents(percents);
        // when & then
        assertThat(splitBuyPercents.calculateBuyPriceBySplitBuyPercents(currentPrice, 0)).isEqualTo(currentPrice - (currentPrice * twoPercent));
        assertThat(splitBuyPercents.calculateBuyPriceBySplitBuyPercents(currentPrice, 1)).isEqualTo(currentPrice - (currentPrice * fivePercent));
        assertThat(splitBuyPercents.calculateBuyPriceBySplitBuyPercents(currentPrice, 2)).isEqualTo(currentPrice - (currentPrice * eightPercent));
    }
}