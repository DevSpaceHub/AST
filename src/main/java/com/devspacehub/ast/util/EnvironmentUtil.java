/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : EnvironmentUtil
 creation : 2024.3.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import lombok.RequiredArgsConstructor;

import static com.devspacehub.ast.common.constant.CommonConstants.*;

@RequiredArgsConstructor
public class EnvironmentUtil {
    public static String getActiveProfile() {
        return System.getProperty("spring.profiles.active");
    }

    public static Boolean isProdActive() {
        if (ACTIVE_PROD.equals(getActiveProfile())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
