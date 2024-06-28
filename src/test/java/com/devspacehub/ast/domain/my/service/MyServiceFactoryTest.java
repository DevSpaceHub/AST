/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceFactoryTest
 creation : 2024.6.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.exception.error.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@ExtendWith(MockitoExtension.class)
class MyServiceFactoryTest {
    @InjectMocks
    MyServiceFactory myServiceFactory;
    @Mock
    MyServiceImpl myService;

    @DisplayName("인자로 Null 값을 전달하면 InvalidValueException이 발생한다.")
    @Test
    void resolveServiceTest_fail() {
        MarketType given = null;
        assertThatThrownBy(() -> myServiceFactory.resolveService(given))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Code: DATA_IS_NULL_ERROR (값이 Null 입니다.)");
    }

    @DisplayName("MarketType에 따라 MyService의 구현체를 반환한다.")
    @Test
    void resolveServiceTest_success() {
        // given
        MarketType given = MarketType.DOMESTIC;
        // when
        MyService result = myServiceFactory.resolveService(given);
        // then
        assertThat(result).isInstanceOf(MyServiceImpl.class);
    }
}