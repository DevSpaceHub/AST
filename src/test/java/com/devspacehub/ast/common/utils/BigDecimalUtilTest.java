/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BigDecimalUtilTest
 creation : 2024.6.7
 author : Yoonji Moon
 */
package com.devspacehub.ast.common.utils;

import com.devspacehub.ast.exception.error.InvalidValueException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BigDecimalUtil 클래스 단위 테스트
 */
@Slf4j
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
        assertThat(onePercentResult).isEqualByComparingTo(new BigDecimal("0.01"));
        assertThat(tenPercentResult).isEqualByComparingTo(new BigDecimal("0.1"));
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

        log.info("new 생성자를 이용해 Float을 바로 BigDecimal로 : {}", floatToBigDecimalByNew);
        log.info("valueOf() 을 이용해 Float을 바로 BigDecimal로 : {}", floatToBigDecimalByValueOf);
        log.info("Float을 String으로 변환 후 BigDecimal로 : {}", floatToStringToBigDecimal);
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

    @DisplayName("연산자 a, b와 자릿수 값 0을 전달하면 소수점 자릿수가 없는 결과 값을 반환한다.")
    @Test
    void divideWithDecimalPlaces_decimalPlaces_is_zero() {
        // given
        BigDecimal a = BigDecimal.valueOf(49999);
        BigDecimal b = BigDecimal.valueOf(50);
        int decimalPlaces = 0;
        // when
        BigDecimal result = BigDecimalUtil.divide(a, b, decimalPlaces);
        // then
        assertThat(result).isEqualTo(BigDecimal.valueOf(999));
    }

    @DisplayName("연산자 a, b와 자릿수 값 2을 전달하면 소수점 두번째 자릿수 결과 값을 반환한다.")
    @Test
    void divideWithDecimalPlaces_decimalPlaces_is_two() {
        // given
        BigDecimal a = BigDecimal.valueOf(49999);
        BigDecimal b = BigDecimal.valueOf(50);
        int decimalPlaces = 2;
        // when
        BigDecimal result = BigDecimalUtil.divide(a, b, decimalPlaces);
        // then
        assertThat(result).isEqualTo(new BigDecimal("999.98"));
    }

    @DisplayName("a가 b보다 작거나 같으면 True를 반환한다.")
    @Test
    void isLessThanOrEqualTo() {
        // given
        BigDecimal a = new BigDecimal("999.98");
        BigDecimal equalToA = new BigDecimal("999.98");
        BigDecimal lessThanA = new BigDecimal("999.976");
        // when
        boolean result1 = BigDecimalUtil.isLessThanOrEqualTo(equalToA, a);
        boolean result2 = BigDecimalUtil.isLessThanOrEqualTo(lessThanA, a);
        // then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
    }

    @DisplayName("BigDecimal 타입 a와 숫자 타입 b를 곱셈하여 소수점 네자리의 BigDecimal 값을 반환한다.")
    @Test
    void multiplyBigDecimalWithNumber() {
        // given
        BigDecimal bigDecimal = new BigDecimal("123.99");
        int number = 30;
        // when
        BigDecimal result = BigDecimalUtil.multiplyBigDecimalWithNumber(bigDecimal, number);
        // then
        assertThat(result).isEqualTo(new BigDecimal("3719.7000"));
    }

    @DisplayName("isLessThanOrEqualTo: a 혹은 b 값이 null이면 InvalidValueException이 발생한다.")
    @NullSource
    @ParameterizedTest
    void isLessThanOrEqualTo_exception(BigDecimal a) {
        // given
        BigDecimal b = new BigDecimal("999.98");

        // when // then
        assertThatThrownBy(() -> BigDecimalUtil.isLessThanOrEqualTo(a, b))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Code: DATA_IS_NULL_ERROR (값이 Null 입니다.)");
    }

    @DisplayName("isLessThan: 두 개의 인자 중 한개라도 값이 null이면 InvalidValueException이 발생한다.")
    @NullSource
    @ParameterizedTest
    void isLessThan_exception(BigDecimal a) {
        // given
        BigDecimal b = new BigDecimal("999.98");

        // when // then
        assertThatThrownBy(() -> BigDecimalUtil.isLessThan(a, b))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Code: DATA_IS_NULL_ERROR (값이 Null 입니다.)");
    }

    @DisplayName("전달하는 연산자 중 한개라도 값이 Null 이라면 InvalidValueException이 발생한다.")
    @NullSource
    @ParameterizedTest
    void nullCheck(BigDecimal givenNull) {
        // given
        BigDecimal a = new BigDecimal("11.00");
        int b = 10;
        // when // then
        assertThatThrownBy(() -> BigDecimalUtil.nullCheck(a, b, givenNull))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Code: DATA_IS_NULL_ERROR (값이 Null 입니다.)");
    }

    @DisplayName("BigDecimal 타입의 값의 소수점 자릿수를 지정한다.")
    @Test
    void setScale() {
        // given
        BigDecimal num = new BigDecimal("11");
        int scale = 3;
        // when
        BigDecimal result = BigDecimalUtil.setScale(num, scale);
        // then
        assertThat(result).isEqualTo(new BigDecimal("11.000"));
    }

}