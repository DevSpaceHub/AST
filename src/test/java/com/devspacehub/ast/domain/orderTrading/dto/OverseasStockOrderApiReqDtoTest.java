/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasStockOrderApiReqDtoTest
 creation : 2024.6.24
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.CommonConstants;
import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OverseasStockOrderApiReqDtoTest {
    @Mock
    private OpenApiProperties openApiProperties;

    @Test
    @DisplayName("계좌번호 정보, 주문할 주식 정보를 인자로 전달하면 해외 주문 요청 DTO를 반환한다.")
    void fromTest() {
        // given
        given(openApiProperties.getAccntNumber()).willReturn("1111111");
        given(openApiProperties.getAccntProductCode()).willReturn("01");

        StockItemDto.Overseas stockItem = StockItemDto.Overseas.builder()
                .itemCode("APPL")
                .orderDivision(CommonConstants.ORDER_DIVISION)
                .orderPrice(new BigDecimal("172.0325"))
                .orderQuantity(1)
                .exchangeCode(ExchangeCode.NASDAQ)
                .build();
        // when
        OverseasStockOrderApiReqDto result = OverseasStockOrderApiReqDto.from(openApiProperties, stockItem);
        // then
        assertThat(result.getOrdSvrDvsnCd()).isEqualTo("0");
        assertThat(result.getOrderPrice()).isEqualTo("172.0325");
        assertThat(result.getExchangeCode()).isEqualTo("NASD");
        assertThat(result.getAccntNumber()).isEqualTo("1111111");
        assertThat(result.getAccntProductCode()).isEqualTo("01");
    }
}