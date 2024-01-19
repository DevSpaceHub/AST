/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyOrderServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.exception.error.NotEnoughCashException;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_BUY_ORDER;

/**
 * 주식 주문 서비스 구현체 - 매수
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BuyOrderServiceImpl extends TradingService {
    private final OpenApiRequest openApiRequest;
    private final OpenApiProperties openApiProperties;
    private final OrderTradingRepository orderTradingRepository;
    private final MyService myService;

    @Value("${openapi.rest.header.transaction-id.buy-order}")
    private String txIdBuyOrder;

    /**
     * 국내주식 매수 주문
     * @param stockItem
     * : stockCode 종목코드(6자리) / orderDivision 주문구분(지정가,00) / orderQuantity 주문수량 / orderPrice 주문단가
     */
    @Override
    public DomesticStockOrderExternalResDto order(StockItemDto stockItem) {
        int realOrderPrice = stockItem.getOrderPrice() * stockItem.getOrderQuantity();
        // 1. valid check
        // TODO 가격 : 종가 +30%, -30% 사이

        // 매수 가능 여부 판단
        if (!myService.buyOrderPossibleCheck(stockItem.getStockCode(), stockItem.getOrderDivision(), realOrderPrice)) {
            throw new NotEnoughCashException();
        }

        // 2. buy order
        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyOrder);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(stockItem.getStockCode())
                .orderDivision(stockItem.getOrderDivision())
                .orderQuantity(String.valueOf(stockItem.getOrderQuantity()))
                .orderPrice(String.valueOf(stockItem.getOrderPrice()))
                .build();
        DomesticStockOrderExternalResDto response = (DomesticStockOrderExternalResDto) openApiRequest.httpPostRequest(DOMESTIC_STOCK_BUY_ORDER, httpHeaders, bodyDto);

        log.info("===== order trading finish =====");
        return response;
    }

    private boolean isStockMarketClosed(String messageCode) {
        return "40100000".equals(messageCode);
    }

    /**
     * 알고리즘에 따라 매수할 종목 선택
     * @param resDto
     * @return
     */
    public List<StockItemDto> pickStockItems(WebClientCommonResDto resDto) {
        // 거래량 순위 종목 (TODO 10개만 고려?)
        DomStockTradingVolumeRankingExternalResDto stockItems = (DomStockTradingVolumeRankingExternalResDto) resDto;

        List<StockItemDto> pickedStockItems = new ArrayList<>();
        // TODO 매수 종목 선택 알고리즘
        for (DomStockTradingVolumeRankingExternalResDto.StockInfo stockItem : stockItems.getStockInfos()) {
            // TODO 매수 금액 결정 위해 주식 현재가 시세 API 호출

            pickedStockItems.add(StockItemDto.builder()
                    .stockCode(stockItem.getMkscShrnIscd())
                    .orderDivision(ORDER_DIVISION)
                    .orderQuantity("")    // TODO 수량, 주문 논의 예정
                    .orderPrice("")
                    .build());
        }

        return pickedStockItems;
    }

    @Transactional
    @Override
    public void saveInfos(List<OrderTrading> orderTradingInfos) {
        orderTradingRepository.saveAll(orderTradingInfos);
    }

}
