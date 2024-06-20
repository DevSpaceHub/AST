/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OpenApiTypeTest
 creation : 2024.6.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

import com.devspacehub.ast.exception.error.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenApiTypeTest {

    @DisplayName("OpenApiType의 code 필드 중 일치하는 값이 없으면 InvalidValueException이 발생한다.")
    @Test
    void fromCode_fail() {
        // given
        String givenCode = "OV000";
        // when // then
        assertThatThrownBy(() -> OpenApiType.fromCode(givenCode))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Code: INVALID_OPENAPI_TYPE_ERROR (해당하는 OpenApiType이 존재하지 않습니다.)");
    }
    @DisplayName("OpenApiType의 code 필드 중 일치하는 값의 Enum 객체를 반환한다.")
    @Test
    void fromCode_success() {
        // given
        String givenCode = "OV001";
        // when
        OpenApiType result = OpenApiType.fromCode(givenCode);
        // then
        assertThat(result).isEqualTo(OpenApiType.OVERSEAS_STOCK_BUY_ORDER);
    }
}