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

import java.math.BigDecimal;

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
        final BigDecimal underTwoThousand = new BigDecimal(1999);
        final BigDecimal underFiveThousand = new BigDecimal(4999);
        final BigDecimal underTwentyThousand = new BigDecimal(19999);
        final BigDecimal underFiftyThousand = new BigDecimal(49999);
        final BigDecimal underTwoHundredThousand = new BigDecimal(199999);
        final BigDecimal underFiveHundredThousand = new BigDecimal(499999);
        final BigDecimal overFiveHundredThousand = new BigDecimal(500000);

        assertThat(StockPriceUnit.getDomesticPriceUnitBy(underTwoThousand)).isEqualTo(1);
        assertThat(StockPriceUnit.getDomesticPriceUnitBy(underFiveThousand)).isEqualTo(5);
        assertThat(StockPriceUnit.getDomesticPriceUnitBy(underTwentyThousand)).isEqualTo(10);
        assertThat(StockPriceUnit.getDomesticPriceUnitBy(underFiftyThousand)).isEqualTo(50);
        assertThat(StockPriceUnit.getDomesticPriceUnitBy(underTwoHundredThousand)).isEqualTo(100);
        assertThat(StockPriceUnit.getDomesticPriceUnitBy(underFiveHundredThousand)).isEqualTo(500);
        assertThat(StockPriceUnit.getDomesticPriceUnitBy(overFiveHundredThousand)).isEqualTo(1000);
    }

    @Test
    @DisplayName("현재가는 0 미만일 수 없다.")
    void getPriceUnitBy_fail() {
        final BigDecimal underZero = BigDecimal.valueOf(-1);

        assertThatThrownBy(() -> StockPriceUnit.getDomesticPriceUnitBy(underZero))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Code: INVALID_CURRENT_PRICE (유효하지 않은 현재가 입니다.)");

    }

    @Test
    @DisplayName("전달한 호가 단위에 맞춰서 현재가의 자릿수를 세팅한다.")
    void orderPriceCuttingByPriceUnit_twoArguments() {
        // given
        final BigDecimal currentPriceUnder2000 = BigDecimal.valueOf(1999);
        final BigDecimal currentPriceUnder5000 = BigDecimal.valueOf(4999);
        final BigDecimal currentPriceUnder20000 = BigDecimal.valueOf(19999);
        final BigDecimal currentPriceUnder50000 = BigDecimal.valueOf(49999);
        final BigDecimal currentPriceUnder200000 = BigDecimal.valueOf(199999);
        final BigDecimal currentPriceUnder500000 = BigDecimal.valueOf(499999);
        final BigDecimal currentPriceOver500000 = BigDecimal.valueOf(500000);

        // when
        BigDecimal result1 = intOrderPriceCuttingByPriceUnit(currentPriceUnder2000, BigDecimal.valueOf(1));
        BigDecimal result2 = intOrderPriceCuttingByPriceUnit(currentPriceUnder5000, BigDecimal.valueOf(5));
        BigDecimal result3 = intOrderPriceCuttingByPriceUnit(currentPriceUnder20000, BigDecimal.valueOf(10));
        BigDecimal result4 = intOrderPriceCuttingByPriceUnit(currentPriceUnder50000, BigDecimal.valueOf(50));
        BigDecimal result5 = intOrderPriceCuttingByPriceUnit(currentPriceUnder200000, BigDecimal.valueOf(100));
        BigDecimal result6 = intOrderPriceCuttingByPriceUnit(currentPriceUnder500000, BigDecimal.valueOf(500));
        BigDecimal result7 = intOrderPriceCuttingByPriceUnit(currentPriceOver500000, BigDecimal.valueOf(1000));
        // then
        assertThat(result1).isEqualByComparingTo(BigDecimal.valueOf(1999));
        assertThat(result2).isEqualByComparingTo(BigDecimal.valueOf(4995));
        assertThat(result3).isEqualByComparingTo(BigDecimal.valueOf(19990));
        assertThat(result4).isEqualByComparingTo(BigDecimal.valueOf(49950));
        assertThat(result5).isEqualByComparingTo(BigDecimal.valueOf(199900));
        assertThat(result6).isEqualByComparingTo(BigDecimal.valueOf(499500));
        assertThat(result7).isEqualByComparingTo(BigDecimal.valueOf(500000));

    }

    @DisplayName("전달한 주문가를 주문가에 맞는 호가 단위에 따라 조정하여 int 타입으로 반환한다.")
    @Test
    void orderPriceCuttingByPriceUnit_oneArguments() {
        // given
        BigDecimal given = BigDecimal.valueOf(40110);
        // when
        BigDecimal result = StockPriceUnit.intOrderPriceCuttingByPriceUnit(given);
        // then
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(40100));
    }

}