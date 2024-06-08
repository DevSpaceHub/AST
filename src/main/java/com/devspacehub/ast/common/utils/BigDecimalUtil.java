/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : BigDecimalUtil
 creation : 2024.6.7
 author : Yoonji Moon
 */
package com.devspacehub.ast.common.utils;

import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.exception.error.InvalidValueException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * BigDecimal 관련 유틸성 클래스
 */
public class BigDecimalUtil {
    /**
     * int 타입 퍼센트를 소수로 변환한다.
     * @param percentage
     * @return
     */
    public static BigDecimal percentageToDecimal (BigDecimal percentage) {
        return divideWithDecimalPlaces(percentage, new BigDecimal("100.0"), 4);
    }

    /**
     * BigDecimal 타입의 값과 int 타입의 값을 곱하여 반환한다.
     * @param a BigDecimal 타입의 값
     * @param b int 타입의 값
     */
    public static BigDecimal multiplyBigDecimalWithNumber(BigDecimal a, int b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    /**
     * a를 b로 나누고, decimalPlaces 값만큼 소수점 자릿수를 맞추어 반환한다.
     * @param a 나누어지는 수
     * @param b 나누는 수
     * @param decimalPlaces 나눗셈 결과의 소수점 자릿수
     * @return
     */
    public static BigDecimal divideWithDecimalPlaces(BigDecimal a, BigDecimal b, int decimalPlaces) {
        if (decimalPlaces == 0) {
            return a.divide(b, 0, RoundingMode.DOWN);
        }
        return a.divide(b, decimalPlaces, RoundingMode.HALF_UP);
    }

    /**
     * a가 b 보다 작으면 True를 반환한다.
     * @param a b보다 더 적은지 체크되는 값
     * @param b a보다 더 큰지 체크되는
     * @return
     */
    public static boolean isLessThan(BigDecimal a, BigDecimal b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            throw new InvalidValueException(ResultCode.NUMBER_IS_NULL);
        }
        return a.compareTo(b) < 0;
    }

}
