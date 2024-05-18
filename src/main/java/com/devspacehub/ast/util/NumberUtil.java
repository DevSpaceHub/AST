/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NumberUtil
 creation : 2024.2.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

/**
 * 숫자 관련 유틸성 클래스.
 */
public class NumberUtil {
    /**
     * int 타입 퍼센트를 소수로 변환한다.
     * @param percentage
     * @return
     */
    public static Float percentageToDecimal (int percentage) {
        return percentage / 100.0F;
    }

}
