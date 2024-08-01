/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : DecimalScale
 creation : 2024.7.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import com.devspacehub.ast.common.utils.BigDecimalUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * 소수점 이하 자릿수 크기 Enum.
 */
@Getter
@RequiredArgsConstructor
public enum DecimalScale {
    ZERO(0),
    TWO(2),
    FOUR(4);

    private final int code;

    /**
     * 현재가 값에 따른 소수점 자릿수 반환한다.
     * $1 미만 주식의 경우, 소수점 네자릿수로 세팅 필요.
     * $1 이상 주식의 경우, 소수점 두자릿수로 세팅 필요.
     * @param currentPrice 현재가
     * @return 현재가 값에 따른 주문가의 소수점 자릿수
     */
    public static int getOrderPriceDecimalScale(BigDecimal currentPrice) {
        if (BigDecimalUtil.isLessThan(currentPrice, BigDecimal.valueOf(1))) {
            return FOUR.code;
        }
        return TWO.code;
    }

}
