/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MarketStatusService
 creation : 2024.1.4
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.domain.marketStatus.dto.*;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.CURRENT_STOCK_PRICE;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMSTOCK_TRADING_VOLUME_RANKING;

/**
 * 주식 현황 조회 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketStatusService {
    private final OpenApiProperties openApiProperties;
    private final OpenApiRequest openApiRequest;
    private final ObjectMapper objectMapper;
    @Value("${openapi.rest.header.transaction-id.trading-volume-ranking-find}")
    private String txIdTradingVolumeRankingFind;
    @Value("${openapi.rest.header.transaction-id.current-stock-price-find}")
    private String txIdCurrentStockPriceFind;

    /**
     * 거래량 조회 (1-10위)
     *
     * @return the list
     */
    public DomStockTradingVolumeRankingExternalResDto findTradingVolume() {
        Consumer<HttpHeaders> httpHeaders = DomStockTradingVolumeRankingExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdTradingVolumeRankingFind);

        MultiValueMap<String, String> queryParams = DomStockTradingVolumeRankingExternalReqDto.createParameter();
        DomStockTradingVolumeRankingExternalResDto response = (DomStockTradingVolumeRankingExternalResDto) openApiRequest.httpGetRequest(
                DOMSTOCK_TRADING_VOLUME_RANKING, httpHeaders, queryParams);

        if (!response.isSuccess()) {
            throw new OpenApiFailedResponseException();
        }
        return response;
    }

    /**
     *  profile = 'local'인 경우 json 파일에서 데이터 읽어온다.
     *
     * @return the trading volume local data
     * @throws IOException the io exception
     */
    @Profile("local")
    public DomStockTradingVolumeRankingExternalResDto getTradingVolumeLocalData() throws IOException {
            File file = ResourceUtils.getFile("classpath:sampleTradingVolumeGetData.json");
            FileInputStream inputStream = new FileInputStream(file);
            String response = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            return objectMapper.readValue(
                    response,
                    DomStockTradingVolumeRankingExternalResDto.class);
    }

    /**
     * 국내 주식 현재가 시세 조회
     */
    public CurrentStockPriceExternalResDto getCurrentStockPrice(String stockCode) {
        Consumer<HttpHeaders> httpHeaders = CurrentStockPriceExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdCurrentStockPriceFind);
        MultiValueMap<String, String> queryParams = CurrentStockPriceExternalReqDto.createParameter(stockCode);

        CurrentStockPriceExternalResDto response = (CurrentStockPriceExternalResDto) openApiRequest.httpGetRequest(CURRENT_STOCK_PRICE, httpHeaders, queryParams);

        if (!response.isSuccess()) {
            throw new OpenApiFailedResponseException();
        }
        return response;
    }

}
