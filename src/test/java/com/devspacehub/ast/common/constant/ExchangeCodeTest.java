/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ExchangeCodeTest
 creation : 2024.7.27
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeCodeTest {

    @CsvSource({
            "NASDAQ,    true",
            "NEWYORK,   true",
            "KOSPI,    false",
            "KOSDAQ,   false",
            "KONEX,    false",
    })
    @ParameterizedTest
    void return_ture_when_exchangeCode_is_overseas(ExchangeCode given, boolean expected) {
        boolean result = given.isOverseas();

        assertThat(result).isEqualTo(expected);
    }
}