/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ProfileTypeTest
 creation : 2024.4.25
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileTypeTest {
    @DisplayName("시스템 프로퍼티로 설정한 값을 가져와서 어떤 환경인지 확인한다.")
    @Test
    void getActiveProfile() {
        //given
        System.setProperty("spring.profiles.active", "test");
        // when
        ProfileType result = ProfileType.getActiveProfileType();
        // then
        assertThat(result).isEqualTo(ProfileType.TEST);
    }


    @Test
    @DisplayName("프로필이 'prod'인지 확인한다.")
    void isProdActive() {
        //given
        System.setProperty("spring.profiles.active", "prod");
        // when
        Boolean isProdActive = ProfileType.isProdActive();
        // then
        assertThat(isProdActive).isTrue();
    }

    @DisplayName("프로필이 'prod'이면 계좌 상태는 '실전'을 반환한다.")
    @Test
    void getAccountStatus() {
        //given
        System.setProperty("spring.profiles.active", "prod");
        // when
        String accountStatus = ProfileType.getAccountStatus();
        // then
        assertThat(accountStatus).isEqualTo("실전");
    }
}