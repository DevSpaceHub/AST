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
import static com.devspacehub.ast.common.constant.StockPriceUnit.*;
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

    @Test
    @DisplayName("전달한 호가 단위에 맞춰서 현재가의 자릿수를 세팅한다.")
    void orderPriceCuttingByPriceUnit_twoArguments() {

        final int currentPriceUnder2000 = 1999;
        final int currentPriceUnder5000 = 4999;
        final int currentPriceUnder20000 = 19999;
        final int currentPriceUnder50000 = 49999;
        final int currentPriceUnder200000 = 199999;
        final int currentPriceUnder500000 = 499999;
        final int currentPriceOver500000 = 500000;

        assertThat(StockPriceUnit.orderPriceCuttingByPriceUnit(currentPriceUnder2000, ONE.getCode())).isEqualTo(1999);
        assertThat(StockPriceUnit.orderPriceCuttingByPriceUnit(currentPriceUnder5000, FIVE.getCode())).isEqualTo(4995);
        assertThat(StockPriceUnit.orderPriceCuttingByPriceUnit(currentPriceUnder20000, TEN.getCode())).isEqualTo(19990);
        assertThat(StockPriceUnit.orderPriceCuttingByPriceUnit(currentPriceUnder50000, FIFTY.getCode())).isEqualTo(49950);
        assertThat(StockPriceUnit.orderPriceCuttingByPriceUnit(currentPriceUnder200000, HUNDRED.getCode())).isEqualTo(199900);
        assertThat(StockPriceUnit.orderPriceCuttingByPriceUnit(currentPriceUnder500000, FIVE_HUNDRED.getCode())).isEqualTo(499500);
        assertThat(StockPriceUnit.orderPriceCuttingByPriceUnit(currentPriceOver500000, THOUSAND.getCode())).isEqualTo(500000);
    }

    @DisplayName("전달한 주문가를 주문가에 맞는 호가 단위에 따라 조정한다.")
    @Test
    void orderPriceCuttingByPriceUnit_oneArguments() {
        // given
        int given = 40110;
        // when
        int result = StockPriceUnit.orderPriceCuttingByPriceUnit(given);
        // then
        assertThat(result).isEqualTo(40100);
    }
}