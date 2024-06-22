/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasMarketStatusServiceTest
 creation : 2024.5.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.marketStatus.dto.OverseasStockConditionSearchResDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * 해외 주식 시장 조회 서비스 클래스 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class OverseasMarketStatusServiceTest {
    @InjectMocks
    private OverseasMarketStatusService overseasMarketStatusService;
    @Mock
    OpenApiRequest openApiRequest;
    @Mock
    private OpenApiProperties openApiProperties;

    @Test
    @DisplayName("NASDAQ, NEWYORK 거래소 코드의 조건검색 API 모두 호출 실패 시 OpenApiFailedResponseException이 발생한다.")
    void getStockConditionSearch_failed() {
        // given
        String givenOpenApiFailedResponseExceptionMessage = "요청 실패하였습니다.(API : 해외주식 조건검색, Exception Message: <NASDAQ> : credentials_type이 유효하지 않습니다.(Bearer)<NEWYORK> : credentials_type이 유효하지 않습니다.(Bearer))";
        String givenBusinessExceptionMessage = String.format("Code: OPENAPI_SERVER_RESPONSE_ERROR (OpenApi 서버에서 오류 응답을 반환하였습니다.)\nException Message: %s", givenOpenApiFailedResponseExceptionMessage);

        OverseasStockConditionSearchResDto givenResponse = new OverseasStockConditionSearchResDto();
        givenResponse.setResultDetails(List.of());
        givenResponse.setMessage("credentials_type이 유효하지 않습니다.(Bearer)");
        givenResponse.setOutput(null);
        given(openApiProperties.getOauth()).willReturn("oauth");
        given(openApiRequest.httpGetRequest(any(OpenApiType.class), any(Consumer.class), any(MultiValueMap.class))).willReturn(givenResponse);

        // when // then
        assertThatThrownBy(() -> overseasMarketStatusService.getStockConditionSearch())
                .isInstanceOf(OpenApiFailedResponseException.class)
                .hasMessage(givenBusinessExceptionMessage);
    }
}