/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NumberUtils
 creation : 2024.8.30
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.utils;

/**
 * 숫자 관련 유틸성 클래스
 */
public class NumberUtils {

    /**
     * 숫자의 왼쪽에 {source}로 채워 원하는 자릿수 만큼 세팅한다.
     * @param original 원래 값
     * @param digits 원하는 자릿수
     * @return 세팅된 값
     */
    public static String padLeftValueWithZeros(String original, String source, int digits) {
        String format = "%" + digits + "s";
        return String.format(format, original).replace(" ", source);
    }
}
