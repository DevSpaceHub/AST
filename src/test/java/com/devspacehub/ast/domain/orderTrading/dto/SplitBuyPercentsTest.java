/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SplitBuyPercentsTest
 creation : 2024.2.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 매수 분할 퍼센트 클래스 단위 테스트
 */
class SplitBuyPercentsTest {

    @Test
    @DisplayName("국내 - 현재가에 대해 분할 매수 퍼센트에 따른 구매 단가를 계산한다.")
    void calculateBuyPriceBySplitBuyPercents_domestic() {
        // given
        final BigDecimal currentPrice = new BigDecimal(99900);
        SplitBuyPercents splitBuyPercents = new SplitBuyPercents(List.of(0.02F, 0.05F, 0.08F));

        // when
        BigDecimal result1 = splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 0);
        BigDecimal result2 = splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 1);
        BigDecimal result3 = splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 2);
        // then
        assertThat(result1).isEqualByComparingTo(new BigDecimal("97902"));
        assertThat(result2).isEqualByComparingTo(new BigDecimal("94905"));
        assertThat(result3).isEqualByComparingTo(new BigDecimal("91908"));
    }
    @Test
    @DisplayName("해외 - 현재가에 대해 분할 매수 퍼센트에 따른 구매 단가를 계산한다.")
    void calculateBuyPriceBySplitBuyPercents_overseas() {
        // given
        final BigDecimal currentPrice = new BigDecimal("25.25");
        SplitBuyPercents splitBuyPercents = new SplitBuyPercents(List.of(0.1F, 0.12F, 0.14F));

        // when
        BigDecimal result0 = splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 0);
        BigDecimal result1 = splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 1);
        BigDecimal result2 = splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, 2);
        // then
        assertThat(result0).isEqualByComparingTo(new BigDecimal("22.725"));
        assertThat(result1).isEqualByComparingTo(new BigDecimal("22.22"));
        assertThat(result2).isEqualByComparingTo(new BigDecimal("21.715"));
    }
}