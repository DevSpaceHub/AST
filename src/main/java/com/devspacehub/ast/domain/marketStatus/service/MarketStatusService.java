/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MarketStatusService
 creation : 2024.1.4
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.ProfileType;
import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.domain.marketStatus.dto.*;
import com.devspacehub.ast.exception.error.DtoConversionException;
import com.devspacehub.ast.exception.error.NotFoundDataException;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.TRADING_VOLUME_RANKING_DATA_SAMPLE_JSON_PATH;
import static com.devspacehub.ast.common.constant.OpenApiType.CURRENT_STOCK_PRICE;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_TRADING_VOLUME_RANKING;

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
    @Value("${openapi.rest.header.transaction-id.domestic.trading-volume-ranking-find}")
    private String txIdTradingVolumeRankingFind;
    @Value("${openapi.rest.header.transaction-id.domestic.current-stock-price-find}")
    private String txIdCurrentStockPriceFind;

    /**
     * 환경에 따라 다른 메서드를 호출하여 거래량 데이터를 반환한다.
     * - 운영 : OpenApi 호출한다.
     * - 테스트 : 서버 경로에 있는 json 데이터에서 읽는다.
     * @return
     */
    public DomStockTradingVolumeRankingExternalResDto getTradingVolumeData() {
        if (ProfileType.isProdActive()) {
            return this.findTradingVolume();
        }
        return this.getTradingVolumeLocalData();
    }

    /**
     * 거래량 조회 (1-10위)
     *
     * @return the list
     */
    private DomStockTradingVolumeRankingExternalResDto findTradingVolume() {
        Consumer<HttpHeaders> httpHeaders = DomStockTradingVolumeRankingExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdTradingVolumeRankingFind);

        MultiValueMap<String, String> queryParams = DomStockTradingVolumeRankingExternalReqDto.createParameter();
        DomStockTradingVolumeRankingExternalResDto response = (DomStockTradingVolumeRankingExternalResDto) openApiRequest.httpGetRequest(
                DOMESTIC_TRADING_VOLUME_RANKING, httpHeaders, queryParams);

        if (response.isFailed()) {
            throw new OpenApiFailedResponseException(DOMESTIC_TRADING_VOLUME_RANKING, response.getMessage());
        }
        return response;
    }

    /**
     *  profile = 'prod' 아닌 경우 서버 내 json 파일에서 데이터 읽어온다.
     *
     * @return the trading volume local data
     * @throws IOException the io exception
     */
    @Profile("!prod")
    private DomStockTradingVolumeRankingExternalResDto getTradingVolumeLocalData() {
        FileInputStream inputStream;
        try {
                File file = ResourceUtils.getFile(TRADING_VOLUME_RANKING_DATA_SAMPLE_JSON_PATH);
                inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
                log.error("Json 파일이 존재하지 않습니다.");
                throw new NotFoundDataException(ResultCode.NOT_FOUND_RANKING_VOLUME_DATA_JSON_FILE);
        }
        String response;
        try {
            response = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
        } catch (IOException e) {
            log.error("InputStream을 String으로 복사하는 과정에서 문제 발생하였습니다.");
            throw new DtoConversionException();
        }
        try {
            return objectMapper.readValue(
                    response,
                    DomStockTradingVolumeRankingExternalResDto.class);
        } catch (JsonProcessingException e) {
            log.error("Json 데이터를 DTO 객체로 변환하는데 실패하였습니다.");
            throw new DtoConversionException();
        }
    }

    /**
     * 국내 주식 현재가 시세 조회
     */
    public CurrentStockPriceExternalResDto.CurrentStockPriceInfo getCurrentStockPrice(String itemCode) {
        Consumer<HttpHeaders> httpHeaders = CurrentStockPriceExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdCurrentStockPriceFind);
        MultiValueMap<String, String> queryParams = CurrentStockPriceExternalReqDto.createParameter(itemCode);

        CurrentStockPriceExternalResDto result = (CurrentStockPriceExternalResDto) openApiRequest.httpGetRequest(CURRENT_STOCK_PRICE, httpHeaders, queryParams);

        if (result.isFailed()) {
            throw new OpenApiFailedResponseException(OpenApiType.CURRENT_STOCK_PRICE, result.getMessage());
        }
        return result.getCurrentStockPriceInfo();
    }
}
