/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ProfileType
 creation : 2024.4.25
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 실행 가능한 Profile 타입.
 */
@Getter
@RequiredArgsConstructor
public enum ProfileType {

    PROD("prod", "실전"),
    TEST("test", "모의");

    private final String code;
    private final String accountStatus;

    /**
     * 실행 환경 별 계좌 상태를 반환한다.
     * @return 계좌 상태
     */
    public static String getAccountStatus() {
        return PROD.code.equals(getActiveProfile()) ? PROD.accountStatus : TEST.accountStatus;
    }

    /**
     * 실행 환경 별 코드를 반환한다.
     * @return 코드
     */
    public static ProfileType getActiveProfileType() {
        return PROD.code.equals(getActiveProfile()) ? PROD : TEST;
    }

    /**
     * 동작 중인 애플리케이션이 'Prod'이면 True를 반환한다. 반대는 False.
     * @return 'Prod' 여부 Boolean 값.
     */
    public static Boolean isProdActive() {
        if (PROD.code.equals(getActiveProfile())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 현재 동작 중인 애플리케이션의 Profile을 반환한다.
     * @return 애플리케이션의 profile
     */
    private static String getActiveProfile() {
        return System.getProperty("spring.profiles.active");
    }
}
