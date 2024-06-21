/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasBuyPossibleCashApiReqDtoTest
 creation : 2024.6.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.request;

import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OverseasBuyPossibleCashApiReqDtoTest {

    @Test
    @DisplayName("BigDecimal 타입의 orderPrice을 인자로 넘기면 MultiValueMap에 저장 시 String 타입으로 저장한다.")
    void createParameter() {
        // given
        String givenOrderPrice = "13.3200";
        MyServiceRequestDto.Overseas giveMyServiceRequestDto = MyServiceRequestDto.Overseas.builder()
                .orderPrice(new BigDecimal(givenOrderPrice))
                .exchangeCode(ExchangeCode.NASDAQ)
                .itemCode("TEST")
                .build();
        // when
        MultiValueMap<String, String> result = OverseasBuyPossibleCashApiReqDto.createParameter(
                "000000", "01", giveMyServiceRequestDto);
        // then
        assertThat(result.getFirst("OVRS_ORD_UNPR")).isEqualTo(givenOrderPrice);
        assertThat(result.getFirst("OVRS_EXCG_CD")).isEqualTo(giveMyServiceRequestDto.getExchangeCode().getLongCode());
        assertThat(result.getFirst("ITEM_CD")).isEqualTo(giveMyServiceRequestDto.getItemCode());
    }
}