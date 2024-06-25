/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasMarketStatusService
 creation : 2024.5.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.marketStatus.dto.OverseasStockConditionSearchReqDto;
import com.devspacehub.ast.domain.marketStatus.dto.OverseasStockConditionSearchResDto;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.ExchangeCode.*;

/**
 * 해외 주식 시장 상태 관련 조회 서비스.
 */
@Service
@RequiredArgsConstructor
public class OverseasMarketStatusService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;

    @Value("${openapi.rest.header.transaction-id.overseas.stock-condition-search}")
    private String txIdStockConditionSearch;
    @Value("${trading.overseas.indicator.minimum-trading-volume}")
    private String minTradingVolume;
    @Value("${trading.overseas.indicator.minimum-market-capital}")
    private String minMarketCapital;
    @Value("${trading.overseas.indicator.minimum-price-earnings-ratio}")
    private String minPER;
    @Value("${trading.overseas.indicator.maximum-price-earnings-ratio}")
    private String maxPER;

    /**
     * 해외 주식 조건 검색 조회 API 를 호출하여 매수 거래 데이터를 반환한다.
     * - 기준 지표 : 거래량, 시가총액, PER
     * @return API의 응답 Response
     */
    public List<OverseasStockConditionSearchResDto> getStockConditionSearch() {
        List<OverseasStockConditionSearchResDto> responses = new ArrayList<>();
        StringBuilder exMessage = new StringBuilder();

        for (ExchangeCode code : List.of(NASDAQ, NEWYORK)) {
            OverseasStockConditionSearchResDto response = callApiWithParameter(code);
            if (response.isFailed()) {
                exMessage.append(String.format("<%s> : %s", code, response.getMessage()));
                continue;
            }
            responses.add(response);
        }
        if (responses.isEmpty()) {
            throw new OpenApiFailedResponseException(OpenApiType.OVERSEAS_STOCK_CONDITION_SEARCH, exMessage.toString());
        }
        return responses;
    }

    /**
     * 헤더/파라미터 생성하여 OpenApi 호출 요청한다.
     * @param code 거래소 코드
     * @return 조건검색 API 응답 Dto
     */
    public OverseasStockConditionSearchResDto callApiWithParameter(ExchangeCode code) {
        Consumer<HttpHeaders> headers = OverseasStockConditionSearchReqDto.setHeaders(openApiProperties.getOauth(), txIdStockConditionSearch);

        OverseasStockConditionSearchReqDto reqDto = OverseasStockConditionSearchReqDto.builder()
                .exchangeCode(code)
                .minTradingVolume(minTradingVolume)
                .minMarketCapital(minMarketCapital)
                .minPER(minPER)
                .maxPER(maxPER)
                .build();
        return (OverseasStockConditionSearchResDto) openApiRequest.httpGetRequest(
                OpenApiType.OVERSEAS_STOCK_CONDITION_SEARCH, headers, reqDto.createParameter());
    }
}
