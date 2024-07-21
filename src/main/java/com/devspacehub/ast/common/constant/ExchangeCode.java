/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ExchangeCode
 creation : 2024.5.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import com.devspacehub.ast.exception.error.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 거래소 코드
 * OpenApi에서 운영하는 거래소코드가 일부 거래소코드에 대해 API마다 다른 값을 이용하고 있음.
 * 이에 따라 필요한 코드(짧은 버전, 긴 버전) 상수를 사용해야 함.
 */
@Getter
@RequiredArgsConstructor
public enum ExchangeCode {
    NASDAQ("NAS", "NASD"),
    NEWYORK("NYS", "NYSE"),
    KOSPI("KOSPI", "KOSPI"),
    KOSDAQ("KOSDAQ", "KOSDAQ"),
    KOSDAQ_GLOBAL("KOSDAQ GLOBAL", "KOSDAQ GLOBAL"),
    KONEX("KONEX", "KONEX")
    ;

    private final String shortCode;
    private final String longCode;

    /**
     * String 타입의 code 값에 매칭되는 Enum 객체를 반환한다.
     * @param code String 타입
     * @return shortCode 혹은 longCode 값이 동일한 Enum 상수
     * @throws InvalidValueException 상수들의 shortCode 혹은 longCode와 동일한 케이스가 없을 경우 발생한다.
     */
    public static ExchangeCode fromCode(String code) {
        return Arrays.stream(ExchangeCode.values())
                .filter(type -> type.getShortCode().equals(code) || type.getLongCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ResultCode.INVALID_EXCHANGE_CODE_ERROR));
    }

    /**
     * JSON 문자열 내 필드 데이터와 Enum의 code 값을 매핑하여 Enum을 반환합니다.
     * @param fieldData JSON 문자열 내 필드 데이터
     * @return shortCode 혹은 longCode 값이 동일한 Enum 상수
     */
    @JsonCreator
    public static ExchangeCode fromJsonField(String fieldData) {
        return Arrays.stream(ExchangeCode.values())
                .filter(type -> type.getShortCode().equals(fieldData) || type.getLongCode().equals(fieldData))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(ResultCode.INVALID_EXCHANGE_CODE_ERROR));
    }

    /**
     * 해외 거래소 코드를 응답한다.
     * @return List 타입의 해외 거래소 코드
     */
    public static List<String> longCodeOverseas() {
        return List.of(ExchangeCode.NASDAQ.getLongCode(), ExchangeCode.NEWYORK.getLongCode());
    }
}
