/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockPriceUnit
 creation : 2024.2.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import com.devspacehub.ast.exception.error.InvalidValueException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 호가 단위 Enum.
 */
@Getter
@RequiredArgsConstructor
public enum StockPriceUnit {
    ONE(1),
    FIVE(5),
    TEN(10),
    FIFTY(50),
    HUNDRED(100),
    FIVE_HUNDRED(500),
    THOUSAND(1000),
    ;

    private final int code;


    /**
     * 주식 가격에 따른 호가 단위 구하기
     */
    public static int getPriceUnitBy(int stockPrice) {
        if (stockPrice < 0) {
            throw new InvalidValueException(ResultCode.INVALID_CURRENT_PRICE);
        }
        if (stockPrice < 2000) {
            return ONE.getCode();
        }
        if (stockPrice < 5000) {
            return FIVE.getCode();
        }
        if (stockPrice < 20000) {
            return TEN.getCode();
        }
        if (stockPrice < 50000) {
            return FIFTY.getCode();
        }
        if (stockPrice < 200000) {
            return HUNDRED.getCode();
        }
        if (stockPrice < 500000) {
            return FIVE_HUNDRED.getCode();
        }
        else {
            return THOUSAND.getCode();
        }
    }
}
