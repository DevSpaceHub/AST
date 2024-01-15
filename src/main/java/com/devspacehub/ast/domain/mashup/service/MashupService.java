/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupService
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingServiceFactory;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Mashup service.
 */
@Service
@RequiredArgsConstructor
public class MashupService {
    private final OAuthService oAuthService;
    private final OrderTradingServiceFactory orderTradingServiceFactory;
    private final MarketStatusService marketStatusService;
    private final MyService myService;
    private final OpenApiProperties openApiProperties;

    @Value("${openapi.rest.header.transaction-id.buy-order}")
    private String txIdBuyOrder;
    @Value("${openapi.rest.header.transaction-id.sell-order}")
    private String txIdSellOrder;

    /**
     * 거래 시작 메서드
     */
    @Transactional
    public void startBuyOrder() {
        // 1. 접근 토큰 발급 or 재사용
        String oauth = oAuthService.issueAccessToken(TokenType.AccessToken);
        openApiProperties.setOauth(oauth);

        // TODO 2. 거래량 조회 api 호출 (상위 10위)
        DomStockTradingVolumeRankingExternalResDto items = marketStatusService.findTradingVolume();

        // TODO 3. stock item selecting logic
        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(OpenApiType.DOMESTIC_STOCK_BUY_ORDER);
        List<StockItemDto> stockItems = tradingService.pickStockItems(items);


        // TODO 4. valid check

        // TODO 주식 주문 시에는 다른 dto를 사용해야할 수도 있음
        // 5. n건 매수
        List<OrderTrading> orderTradings = new ArrayList<>();

        for (StockItemDto item : stockItems) {
            item.setTransactionId(txIdBuyOrder);
            DomesticStockOrderExternalResDto result = tradingService.order(item);
            orderTradings.add(createOrderFromDTOs(item, result));
        }

        // 5. 주문거래 정보 저장
        tradingService.saveInfos(orderTradings);
    }

    @Transactional
    public void startSellOrder() {
        // 1. 접근 토큰 발급 or 재사용
        String oauth = oAuthService.issueAccessToken(TokenType.AccessToken);
        openApiProperties.setOauth(oauth);

        // 2. 주식 잔고 조회
        StockBalanceExternalResDto myStockBalance = myService.getMyStockBalance();

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(OpenApiType.DOMESTIC_STOCK_SELL_ORDER);
        // 3. 매도할 주식 select (손절매도 & 수익매도)
        List<StockItemDto> stockItems = tradingService.pickStockItems(myStockBalance);
        // 4. 매도 주문
        List<OrderTrading> orderTradings = new ArrayList<>();

        for (StockItemDto item : stockItems) {
            item.setTransactionId(txIdSellOrder);
            DomesticStockOrderExternalResDto result = tradingService.order(item);
            orderTradings.add(createOrderFromDTOs(item, result));
        }

        // 5. 주문거래 정보 저장
        tradingService.saveInfos(orderTradings);
    }

    private OrderTrading createOrderFromDTOs(StockItemDto item, DomesticStockOrderExternalResDto result) {
        return OrderTrading.builder()
                .itemCode(item.getStockCode())
                .transactionId(item.getTransactionId())
                .orderDivision(item.getOrderDivision())
                .orderPrice(item.getOrderPrice())
                .orderQuantity(item.getOrderQuantity())
                .orderResultCode(result.getResultCode())
                .orderMessageCode(result.getMessageCode())
                .orderMessage(result.getMessage())
                .orderNumber(result.isSuccess() ? result.getOutput().getOrderNumber() : null)
                .orderDateTime(result.isSuccess() ? result.getOutput().getOrderDateTime() : null)
                .build();
    }

}
