/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasStockConditionSearchReqDtoTest
 creation : 2024.5.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

class OverseasStockConditionSearchReqDtoTest {
    @Test
    @DisplayName("해외주식 조건검색 API 요청 시 NASDAQ의 거래소 코드는 'NASD'로 세팅한다.")
    void createParameter() {
        // given
        ExchangeCode given = ExchangeCode.NASDAQ;
        OverseasStockConditionSearchReqDto reqDto = OverseasStockConditionSearchReqDto.builder()
                .exchangeCode(given)
                .build();
        // when
        MultiValueMap<String, String> result = reqDto.createParameter();
        // then
        assertThat(result.get("EXCD").get(0)).isEqualTo(given.getShortCode());
    }
}