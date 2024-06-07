package com.devspacehub.ast.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalUtilTest {

    @Test
    @DisplayName("정수의 퍼센트 값을 넘기면 소수점으로 변환한다.")
    void percentageToDecimal() {
        // given
        final BigDecimal onePercent = BigDecimal.valueOf(1);
        final BigDecimal tenPercent = BigDecimal.valueOf(10);
        // when
        BigDecimal onePercentResult = BigDecimalUtil.percentageToDecimal(onePercent);
        BigDecimal tenPercentResult = BigDecimalUtil.percentageToDecimal(tenPercent);
        // then
        assertThat(onePercentResult).isEqualTo(new BigDecimal("0.01"));
        assertThat(tenPercentResult).isEqualTo(new BigDecimal("0.1"));
    }

    @DisplayName("a가 b보다 크거나 동일하면 False 반환하고 작으면 True 반환한다.")
    @Test
    void isLessThan() {
        // given
        BigDecimal a = BigDecimal.valueOf(3900);
        BigDecimal b = BigDecimal.valueOf(4000);
        // when
        boolean result1 = BigDecimalUtil.isLessThan(a, a);
        boolean result2 = BigDecimalUtil.isLessThan(a, b);
        // then
        assertThat(result1).isFalse();
        assertThat(result2).isTrue();
    }


    @DisplayName("Float 타입은 직접 BigDecimal로 형변환하면 부동 소수점의 정밀도 문제로 정확한 값으로 형변환되지 않으므로 String으로 변환 후 BigDecimal로 변환한다.")
    @Test
    void floatToBigDecimal_new() {
        // given
        Float givenFloat = 1.22F;
        // when
        BigDecimal floatToBigDecimalByNew = new BigDecimal(givenFloat);
        BigDecimal floatToBigDecimalByValueOf = BigDecimal.valueOf(givenFloat);

        BigDecimal floatToStringToBigDecimal = new BigDecimal(String.valueOf(givenFloat));
        // then
        assertThat(floatToBigDecimalByNew).isNotEqualTo(floatToStringToBigDecimal);
        assertThat(floatToBigDecimalByValueOf).isNotEqualTo(floatToStringToBigDecimal);
        assertThat(floatToStringToBigDecimal.floatValue()).isEqualTo(givenFloat);

        System.out.println("new 생성자를 이용해 Float을 바로 BigDecimal로 : " + floatToBigDecimalByNew);
        System.out.println("valueOf() 을 이용해 Float을 바로 BigDecimal로 : " + floatToBigDecimalByNew);
        System.out.println("Float을 String으로 변환 후 BigDecimal로 : " + floatToStringToBigDecimal);
    }

    @DisplayName("BigDecimal을 int, Float 타입으로 형변환할 수 있다.")
    @Test
    void bigDecimalToIntFloat() {
        // given
        BigDecimal floatType = new BigDecimal(String.valueOf(1.22F));
        BigDecimal intType = BigDecimal.valueOf(100);
        // when
        Float resultFloat = floatType.floatValue();
        int resultInt = intType.intValue();
        // then
        assertThat(resultFloat).isEqualTo(1.22F);
        assertThat(resultInt).isEqualTo(100);
    }
}