/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : EnvironmentUtil
 creation : 2024.3.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import lombok.RequiredArgsConstructor;

import static com.devspacehub.ast.common.constant.CommonConstants.*;

/**
 * 동작하는 애플리케이션의 환경설정값에 대한 Util성 클래스.
 * - Read
 */
@RequiredArgsConstructor
public class EnvironmentUtil {

    /**
     * 현재 동작 중인 애플리케이션의 Profile을 반환한다.
     * @return 애플리케이션의 profile
     */
    public static String getActiveProfile() {
        return System.getProperty("spring.profiles.active");
    }

    /**
     * 동작 중인 애플리케이션이 'Prod'이면 True를 반환한다. 반대는 False.
     * @return 'Prod' 여부 Boolean 값.
     */
    public static Boolean isProdActive() {
        if (ACTIVE_PROD.equals(getActiveProfile())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
