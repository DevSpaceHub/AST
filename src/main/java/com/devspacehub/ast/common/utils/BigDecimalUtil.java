/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BigDecimalUtil
 creation : 2024.6.7
 author : Yoonji Moon
 */
package com.devspacehub.ast.common.utils;

import com.devspacehub.ast.common.constant.CommonConstants;
import com.devspacehub.ast.common.constant.DecimalScale;
import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.exception.error.InvalidValueException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;

/**
 * BigDecimal 관련 유틸성 클래스
 */
public class BigDecimalUtil {
    /**
     * int 타입 퍼센트를 소수로 변환한다.
     * @param percentage 숫자 타입 (%)
     * @return 퍼센트를 소수점으로 변환한 값
     */
    public static BigDecimal percentageToDecimal (BigDecimal percentage) {
        nullCheck(percentage);
        return divide(percentage, new BigDecimal("100.0"), DecimalScale.FOUR.getCode());
    }

    /**
     * BigDecimal 타입의 값과 int 타입의 값을 곱하여 반환한다.
     * @param a BigDecimal 타입의 값
     * @param b int 타입의 값
     */
    public static BigDecimal multiplyBigDecimalWithNumber(BigDecimal a, int b) {
        nullCheck(a, b);
        return a.multiply(BigDecimal.valueOf(b)).setScale(CommonConstants.DECIMAL_SCALE_FOUR, RoundingMode.DOWN);
    }

    /**
     * a를 b로 나누고, scale 값만큼 소수점 자릿수를 맞추어 반환한다.
     * @param a     나누어지는 수
     * @param b     나누는 수
     * @param scale 나눗셈 결과의 소수점 자릿수
     * @return 나눗셈 결과
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
        nullCheck(a, b);
        if (scale == 0) {
            return a.divide(b, 0, RoundingMode.DOWN);
        }
        return a.divide(b, scale, RoundingMode.DOWN);
    }

    /**
     * a가 b 보다 작으면 True를 반환한다.
     * @param a b보다 더 적은지 체크되는 값
     * @param b a보다 더 큰지 체크되는 값
     * @return
     */
    public static boolean isLessThan(BigDecimal a, BigDecimal b) {
        nullCheck(a, b);
        return a.compareTo(b) < 0;
    }

    /**
     * a가 b 보다 작거나 같으면 True를 반환한다.
     * @param a b보다 더 적거나 같은지 체크되는 값
     * @param b a보다 더 크거나 같은지 체크되는 값
     * @return
     */
    public static boolean isLessThanOrEqualTo(BigDecimal a, BigDecimal b) {
        nullCheck(a, b);
        return a.compareTo(b) <= 0;
    }

    /**
     * 연산 작업 전 인자들에 대해 Null 체크한다.
     * @param num (피)연산자
     * @param <T> Number를 상속받는 모든 타입
     * @throws InvalidValueException 값이 한개라도 Null일 경우 발생한다.
     */
    protected static <T extends Number> void nullCheck(T... num) {
        if (Arrays.stream(num).anyMatch(Objects::isNull)) {
            throw new InvalidValueException(ResultCode.DATA_IS_NULL_ERROR);
        }
    }

    /**
     * BigDecimal 타입 값의 소수점 자릿수를 세팅한다.
     * @param num BigDecimal 타입 값
     * @param scale 소수점 자릿수
     * @return {scale} 값에 따라 컷팅된 BigDecimal 타입 값
     */
    public static BigDecimal setScale(BigDecimal num, int scale) {
        return num.setScale(scale, RoundingMode.DOWN);
    }
}
