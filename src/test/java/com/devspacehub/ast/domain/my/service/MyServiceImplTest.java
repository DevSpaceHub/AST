/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MyServiceImplTest
 creation : 2024.4.27
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.my.dto.orderConclusion.DomesticOrderConclusionFindExternalResDto;
import com.devspacehub.ast.domain.my.dto.orderConclusion.OrderConclusionDto;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

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
        DomesticOrderConclusionFindExternalResDto givenOpenApiResponseDto = new DomesticOrderConclusionFindExternalResDto();
        givenOpenApiResponseDto.setResultCode(OPENAPI_SUCCESS_RESULT_CODE);
        givenOpenApiResponseDto.setOutput1(List.of(DomesticOrderConclusionFindExternalResDto.Output1.builder()
                .itemCode("000000")
                .itemNameKor("TEST")
                .orderNumber("11111")
                .totalConcludedPrice("1000")
                .totalConcludedQuantity("1")
                .orderQuantity("1")
                .orderPrice("1000")
                .orderTime("090008")
                .build()));
        givenOpenApiResponseDto.setOutput2(new DomesticOrderConclusionFindExternalResDto.Output2());
        given(openApiProperties.getOauth()).willReturn("oauth");
        given(openApiRequest.httpGetRequestWithExecute(any(OpenApiType.class), any(HttpHeaders.class), any(MultiValueMap.class))).willReturn(ResponseEntity.ok(givenOpenApiResponseDto));

        // when
        List<OrderConclusionDto> result = myServiceImpl.getConcludedStock(LocalDate.now());
        // then
        assertNotNull(result);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItemCode()).isEqualTo("000000");
        assertThat(result.get(0).getOrderNumber()).isEqualTo("0000011111");
    }

    @DisplayName("연쇄적인 API 호출 시 요청 파라미터를 추가 세팅한다.")
    @Test
    void prepareParamsForSequentialApiCalls() {
        MultiValueMap<String, String> result = myServiceImpl.prepareParamsForSequentialApiCalls(new LinkedMultiValueMap<>(), "100001", "200001");

        assertThat(result)
                .hasSize(2)
                .containsEntry("CTX_AREA_NK100", List.of("100001"))
                .containsEntry("CTX_AREA_FK100", List.of("200001"));
    }
}