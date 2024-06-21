/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockPriceUnit
 creation : 2024.2.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import com.devspacehub.ast.common.utils.BigDecimalUtil;
import com.devspacehub.ast.exception.error.InvalidValueException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

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
    public static int getDomesticPriceUnitBy(BigDecimal stockPrice) {
        int intStockPrice = stockPrice.intValue();
        if (intStockPrice < 0) {
            throw new InvalidValueException(ResultCode.INVALID_CURRENT_PRICE);
        }
        if (intStockPrice < 2000) {
            return ONE.getCode();
        }
        if (intStockPrice < 5000) {
            return FIVE.getCode();
        }
        if (intStockPrice < 20000) {
            return TEN.getCode();
        }
        if (intStockPrice < 50000) {
            return FIFTY.getCode();
        }
        if (intStockPrice < 200000) {
            return HUNDRED.getCode();
        }
        if (intStockPrice < 500000) {
            return FIVE_HUNDRED.getCode();
        }
        else {
            return THOUSAND.getCode();
        }
    }

    /**
     * 주문 가격을 인자로 받아서 호가단위에 따라 주문가 조정하여 반환한다.
     * @param orderPrice 주문가
     * @return 호가 단위에 의해 조정된 주문가
     */
    public static BigDecimal intOrderPriceCuttingByPriceUnit(BigDecimal orderPrice) {
        BigDecimal priceUnit = BigDecimal.valueOf(getDomesticPriceUnitBy(orderPrice));
        BigDecimal decimal = BigDecimalUtil.divide(orderPrice, priceUnit, 0);
        return decimal.multiply(priceUnit);
    }

    /**
     * 주문 가격과 호가 단위를 인자로 받아서 호가단위에 따라 주문가 조정하여 int 타입으로 반환한다.
     * 호가 단위로 나누면서 소수점을 절삭해야하므로 나눌 때 scale 값을 0으로 전달한다.
     * @param orderPrice 주문가
     * @param priceUnit 호가 단위
     * @return int 타입. 호가 단위에 의해 조정된 주문가
     */
    public static BigDecimal intOrderPriceCuttingByPriceUnit(BigDecimal orderPrice, BigDecimal priceUnit) {
        return BigDecimalUtil.divide(orderPrice, priceUnit, 0).multiply(priceUnit);
    }

    /**
     * 주문 가격과 호가 단위를 인자로 받아서 호가단위에 따라 주문가 조정하여 Float 타입으로 반환한다.
     * 호가 단위로 나누면서 소수점을 절삭해야하므로 나눌 때 scale 값을 4로 전달한다.
     * @param orderPrice 주문가
     * @param scale 현재가에 의해 결정된 소수점 자릿수 (호가 단위)
     * @return Float 타입. 호가 단위에 의해 조정된 주문가
     */
    public static BigDecimal floatOrderPriceCuttingByDecimalScale(BigDecimal orderPrice, int scale) {
        return BigDecimalUtil.setScale(orderPrice, scale);
    }
}
