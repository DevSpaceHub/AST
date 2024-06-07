package com.devspacehub.ast.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public static BigDecimal multiplyBigDecimalWithNumber(BigDecimal a, int b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal divideWithDecimalPlaces(BigDecimal a, BigDecimal b, int decimalPlaces) {
        if (decimalPlaces == 0) {
            return a.divide(b, 0, RoundingMode.DOWN);
        }
        return a.divide(b, decimalPlaces, RoundingMode.HALF_UP);
    }

    public static boolean isLessThan(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

}
