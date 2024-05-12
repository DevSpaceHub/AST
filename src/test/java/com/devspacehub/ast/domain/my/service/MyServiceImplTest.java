/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceImplTest
 creation : 2024.4.27
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.orderConclusion.dto.OrderConclusionFindExternalResDto;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MyServiceImplTest {
    @InjectMocks
    private MyServiceImpl myServiceImpl;

    @Spy
    private OpenApiProperties openApiProperties;
    @Mock
    private OpenApiRequest openApiRequest;

    @DisplayName("체결 종목 API를 호출하여 주문 체결 정보 DTO로 변환하여 반환한다.")
    @Test
    void getConcludedStock() {
        // given
        OrderConclusionFindExternalResDto givenOpenApiResponseDto = OrderConclusionFindExternalResDto.builder()
                .resultCode(OPENAPI_SUCCESS_RESULT_CODE)
                .output1(List.of(OrderConclusionFindExternalResDto.Output1.builder()
                        .itemCode("000000")
                        .itemNameKor("TEST")
                        .orderNumber("11111")
                        .totalConcludedPrice("1000")
                        .totalConcludedQuantity("1")
                        .orderQuantity("1")
                        .orderPrice("1000")
                        .orderTime("090008")
                        .build()))
                .output2(new OrderConclusionFindExternalResDto.Output2())
                .build();
        given(openApiProperties.getOauth()).willReturn("oauth");
        given(openApiRequest.httpGetRequest(any(OpenApiType.class), any(Consumer.class), any(MultiValueMap.class))).willReturn(givenOpenApiResponseDto);

        // when
        List<OrderConclusionDto> result = myServiceImpl.getConcludedStock(LocalDate.now());
        // then
        assertNotNull(result);
        assertThat(result).hasSize(1);
        assertThat(givenOpenApiResponseDto.getOutput1().get(0).getItemCode()).isEqualTo(result.get(0).getItemCode());
    }


    @DisplayName("나의 예수금이 주문 금액보다 낮은지 확인한다.")
    @Test
    void isMyDepositLowerThanOrderPrice() {
        // given
        int givenMyDeposit = 1000;
        int givenOrderPrice = 1000;
        // when
        boolean result = myServiceImpl.isMyDepositLowerThanOrderPrice(givenMyDeposit, givenOrderPrice);
        // then
        assertThat(result).isFalse();
    }
}