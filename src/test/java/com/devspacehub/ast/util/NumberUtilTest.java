/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NumberUtilTest
 creation : 2024.2.23
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberUtilTest {

    @Test
    @DisplayName("정수의 퍼센트 값을 소수점으로 변환한다.")
    void percentageToDecimal() {
        // given
        final int onePercent = 1;
        final int tenPercent = 10;
        // when
        Float onePercentResult = NumberUtil.percentageToDecimal(onePercent);
        Float tenPercentResult = NumberUtil.percentageToDecimal(tenPercent);
        // then
        assertThat(onePercentResult).isEqualTo(0.01F);
        assertThat(tenPercentResult).isEqualTo(0.1F);
    }
}