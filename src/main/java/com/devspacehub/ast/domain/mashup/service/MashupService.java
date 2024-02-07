/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupService
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.mashup.service;

import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.dto.response.StockBalanceExternalResDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingServiceFactory;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.exception.error.NotFoundDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.devspacehub.ast.common.constant.CommonConstants.ACTIVE_BETA;
import static com.devspacehub.ast.common.constant.CommonConstants.ACTIVE_LOCAL;
import static com.devspacehub.ast.common.constant.OpenApiType.*;

/**
 * The type Mashup service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MashupService {
    private final OAuthService oAuthService;
    private final OrderTradingServiceFactory orderTradingServiceFactory;
    private final MarketStatusService marketStatusService;
    private final MyService myService;
    private final Notificator notificator;
    private final Environment environment;

    @Value("${openapi.rest.header.transaction-id.buy-order}")
    private String txIdBuyOrder;
    @Value("${openapi.rest.header.transaction-id.sell-order}")
    private String txIdSellOrder;
    private static final String REAL_ACCOUNT_STATUS_KOR = "실전";
    private static final String TEST_ACCOUNT_STATUS_KOR = "모의";

    /**
     * 매수 주문
     */
    @Transactional
    public void startBuyOrder() {
        oAuthService.setAccessToken(TokenType.AccessToken);

        // 1. 거래량 조회 (상위 10위)
        String accountStatusKor;
        DomStockTradingVolumeRankingExternalResDto items;
        if (Boolean.TRUE.equals(isProdActive())) {
            items = marketStatusService.findTradingVolume();
            accountStatusKor = REAL_ACCOUNT_STATUS_KOR;
        } else {
            try {
                accountStatusKor = TEST_ACCOUNT_STATUS_KOR;
                items = marketStatusService.getTradingVolumeLocalData();
            } catch (IOException ex) {
                log.error("프로젝트 내 Json 파일을 읽는데 실패하였습니다.");
                throw new NotFoundDataException(ResultCode.NOT_FOUND_RANKING_VOLUME_DATA_JSON_FILE);
            }
        }

        // 2. 종목 선택 (거래량 순위 API) 및 매입수량 결정 (현재가 시세 조회 API)
        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(DOMESTIC_STOCK_BUY_ORDER);
        List<StockItemDto> stockItems = tradingService.pickStockItems(items);
        log.info("매수 선택 종목 : {}", stockItems.size());

        // 3. 매수
        List<OrderTrading> orderTradings = new ArrayList<>();
        for (StockItemDto item : stockItems) {
            DomesticStockOrderExternalResDto result = tradingService.order(item);
            OrderTrading orderTrading = createOrderFromDTOs(item, result, txIdBuyOrder);
            orderTradings.add(orderTrading);

            if (result.isSuccess()) {
                notificator.sendMessage(DOMESTIC_STOCK_BUY_ORDER, accountStatusKor, orderTrading);
            }
        }
        // 4. 주문거래 정보 저장
        tradingService.saveInfos(orderTradings);
    }

    private Boolean isProdActive() {
        for(String profile : environment.getActiveProfiles()) {
            if (ACTIVE_LOCAL.equals(profile) || ACTIVE_BETA.equals(profile)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 매도 주문
     */
    @Transactional
    public void startSellOrder() {
        oAuthService.setAccessToken(TokenType.AccessToken);

        // 1. 주식 잔고 조회
        StockBalanceExternalResDto myStockBalance = myService.getMyStockBalance();

        TradingService tradingService = orderTradingServiceFactory.getServiceImpl(DOMESTIC_STOCK_SELL_ORDER);
        // 2. 주식 선택 후 매도 주문 (손절매도 & 수익매도)
        List<OrderTrading> orderTradings = new ArrayList<>();
        for (StockItemDto item : tradingService.pickStockItems(myStockBalance)) {
            DomesticStockOrderExternalResDto result = tradingService.order(item);
            OrderTrading orderTrading = createOrderFromDTOs(item, result, txIdSellOrder);
            orderTradings.add(orderTrading);

            if (result.isSuccess()) {
                String accountStatusKor = Boolean.TRUE.equals(isProdActive()) ? REAL_ACCOUNT_STATUS_KOR : TEST_ACCOUNT_STATUS_KOR;
                notificator.sendMessage(DOMESTIC_STOCK_SELL_ORDER, accountStatusKor, orderTrading);
            }
        }

        // 3. 주문한 것 있으면 주문 거래 정보 저장
      tradingService.saveInfos(orderTradings);
    }

    private OrderTrading createOrderFromDTOs(StockItemDto item, DomesticStockOrderExternalResDto result, String txId) {
        return OrderTrading.builder()
                .itemCode(item.getStockCode())
                .itemNameKor(item.getStockNameKor())
                .transactionId(txId)
                .orderDivision(item.getOrderDivision())
                .orderPrice(item.getCurrentStockPrice())
                .orderQuantity(item.getOrderQuantity())
                .orderResultCode(result.getResultCode())
                .orderMessageCode(result.getMessageCode())
                .orderMessage(result.getMessage())
                .orderNumber(result.isSuccess() ? result.getOutput().getOrderNumber() : null)
                .orderDateTime(result.isSuccess() ? result.getOutput().getOrderDateTime() : null)
                .build();
    }

}
