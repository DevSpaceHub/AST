/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : EnvironmentUtilTest
 creation : 2024.3.25
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentUtilTest {

    @DisplayName("시스템 프로퍼티로 설정한 값을 가져와서 어떤 환경인지 확인한다.")
    @Test
    void getActiveProfile() {
        //given
        System.setProperty("spring.profiles.active", "local");
        // when
        String activeProfile = EnvironmentUtil.getActiveProfile();
        // then
        assertThat(activeProfile).isEqualTo("local");
    }
}