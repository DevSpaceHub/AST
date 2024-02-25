/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockPriceUnitTest
 creation : 2024.2.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import com.devspacehub.ast.exception.error.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.devspacehub.ast.common.constant.ResultCode.INVALID_CURRENT_PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 호가 단위 Enum 단위 테스트
 */
class StockPriceUnitTest {

    @Test
    @DisplayName("현재가 범위에 따라 호가 단위가 달라진다.")
    void getPriceUnitBy_success() {
        final int underTwoThousand = 1999;
        final int underFiveThousand = 4999;
        final int underTwentyThousand = 19999;
        final int underFiftyThousand = 49999;
        final int underTwoHundredThousand = 199999;
        final int underFiveHundredThousand = 499999;
        final int overFiveHundredThousand = 500000;

        assertThat(StockPriceUnit.getPriceUnitBy(underTwoThousand)).isEqualTo(1);
        assertThat(StockPriceUnit.getPriceUnitBy(underFiveThousand)).isEqualTo(5);
        assertThat(StockPriceUnit.getPriceUnitBy(underTwentyThousand)).isEqualTo(10);
        assertThat(StockPriceUnit.getPriceUnitBy(underFiftyThousand)).isEqualTo(50);
        assertThat(StockPriceUnit.getPriceUnitBy(underTwoHundredThousand)).isEqualTo(100);
        assertThat(StockPriceUnit.getPriceUnitBy(underFiveHundredThousand)).isEqualTo(500);
        assertThat(StockPriceUnit.getPriceUnitBy(overFiveHundredThousand)).isEqualTo(1000);
    }

    @Test
    @DisplayName("현재가는 0 미만일 수 없다.")
    void getPriceUnitBy_fail() {
        final int underZero = -1;

        assertThatThrownBy(() -> StockPriceUnit.getPriceUnitBy(underZero))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(INVALID_CURRENT_PRICE.name() + " : " + INVALID_CURRENT_PRICE.getMessage());

    }
}