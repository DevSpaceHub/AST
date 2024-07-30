/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasSellOrderServiceImpl
 creation : 2024.6.12
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service.overseas;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.MarketType;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.my.service.MyServiceFactory;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.OverseasStockOrderApiReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static com.devspacehub.ast.common.constant.OpenApiType.OVERSEAS_STOCK_SELL_ORDER;
import static com.devspacehub.ast.common.constant.ProfileType.getAccountStatus;

/**
 * 해외 주식 주문 서비스 구현체 - 매도
 */
@Slf4j
@Transactional(readOnly = true)
@Service
public class OverseasSellOrderServiceImpl extends TradingService {
    @Value("${openapi.rest.header.transaction-id.overseas.sell-order}")
    private String transactionId;
    @Value("${trading.overseas.indicator.minimum-loss-figure-ratio}")
    private Float minimumLossFigureRatio;

    @Value("${trading.overseas.indicator.minimum-profit-figure-ratio}")
    private Float minimumProfitFigureRatio;

    private static final int MARKET_START_HOUR = 22;
    private static final int MAKRET_END_HOUR = 6;

    public OverseasSellOrderServiceImpl(OpenApiRequest openApiRequest, Notificator notificator,
                                        OrderTradingRepository orderTradingRepository, MyServiceFactory myServiceFactory) {
        super(openApiRequest, notificator, orderTradingRepository, myServiceFactory);
    }


    /**
     * 해외 매도 주문
     *
     * @param openApiProperties Open Api 호출 시 필요한 값들
     * @param openApiType OpenApi 타입
     * @return 주문 결과로부터 생성된 Entity
     */
    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType) {
        // 1. 주식 잔고 조회
        OverseasStockBalanceApiResDto myStockBalance = (OverseasStockBalanceApiResDto) myServiceImpl().getMyStockBalance();

        // 2. 주식 선택 후 매도 주문 (손절매도 & 수익매도)
        List<OrderTrading> orderTradings = new ArrayList<>();
        for (StockItemDto.Overseas item : pickStockItems(myStockBalance)) {
            StockOrderApiResDto result = callOrderApi(openApiProperties, item, OVERSEAS_STOCK_SELL_ORDER, transactionId);
            OrderTrading orderTrading = OrderTrading.from(item, result, transactionId);
            orderTradings.add(orderTrading);

            orderApiResultProcess(result, orderTrading);
        }
        return orderTradings;
    }

    /**
     * 매도 손절/익절 기준에 따라 매도할 종목을 리스트에 추가하여 반환한다.
     * @param stockBalanceResponse 주식 잔고 응답 Dto
     * @return 매도할 수 있는 주식 종목들
     */
    private List<StockItemDto.Overseas> pickStockItems(OverseasStockBalanceApiResDto stockBalanceResponse) {
        List<StockItemDto.Overseas> pickedStockItems = new ArrayList<>();

        for (OverseasStockBalanceApiResDto.MyStockBalance myStockBalance : stockBalanceResponse.getMyStockBalance()) {
            if (isSellOrderableItem(myStockBalance)) {
                pickedStockItems.add(StockItemDto.Overseas.of(myStockBalance));
            }
        }
        return pickedStockItems;
    }

    /**
     * 매도 가능한 종목인지 체크한다.
     * @param myStockBalance 주식 잔고 조회 Api 응답 Dto.
     * @return 아래 경우에 해당하지 않을 경우 True를 반환한다.
     * 1. 평가손익률이 손절 지표와 익절 지표 사이에 있다면 False
     * 2. 해당 종목에 대해 금일 이미 매도 주문을 했다면 False
     */
    public boolean isSellOrderableItem(OverseasStockBalanceApiResDto.MyStockBalance myStockBalance) {
        // 기준 : 손절 퍼센트보다 낮으면 손절. 익절 퍼센트보다 높으면 익절.
        if (isEvaluateProfitLossRateBetweenProfitAndLossIndicator(myStockBalance.getEvaluateProfitLossRate())) {
            return false;
        }
        return isNewOrder(myStockBalance.getItemCode(), transactionId,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(MARKET_START_HOUR, 0, 0)),
                LocalDateTime.of(LocalDate.now().plusDays(1L), LocalTime.of(MAKRET_END_HOUR, 0, 0)));
    }

    /**
     * 평가손익률이 손절 지표와 익절 지표 사이에 있는지 체크한다.
     * @param evaluateProfitLossRate 평가손익률
     * @return 평가손익률이 손절 지표와 익절 지표 사이에 있으면 True를 반환한다.
     */
    protected boolean isEvaluateProfitLossRateBetweenProfitAndLossIndicator(Float evaluateProfitLossRate) {
        return evaluateProfitLossRate > minimumLossFigureRatio && evaluateProfitLossRate < minimumProfitFigureRatio;
    }

    /**
     * 매도 주문 API를 호출한다.
     * @param openApiProperties OpenApi 호출 시 필수 값들
     * @param stockItem 주식 종목 코드
     * @param openApiType OpenApi 호출 타입
     * @param transactionId 트랜잭션 ID
     * @param <T> StockItemDto를 상속한다.
     * @return 매도 주문 결과 응답 Dto.
     */
    @Override
    public <T extends StockItemDto> StockOrderApiResDto callOrderApi(OpenApiProperties openApiProperties, T stockItem,
                                                                     OpenApiType openApiType, String transactionId) {
        Consumer<HttpHeaders> httpHeaders = OverseasStockOrderApiReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        OverseasStockOrderApiReqDto bodyDto = OverseasStockOrderApiReqDto.from(openApiProperties, stockItem);

        return (StockOrderApiResDto) openApiRequest.httpPostRequest(openApiType, httpHeaders, bodyDto);
    }

    /**
     * 매도 주문 정보 저장
     *
     * @param orderTradingInfos 다수의 매도 주문 정보 Entity
     * @return 저장된 다수의 Entity
     */
    @Transactional
    @Override
    public List<OrderTrading> saveOrderInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            return orderTradingRepository.saveAll(orderTradingInfos);
        }
        return Collections.emptyList();
    }

    /**
     * 매도 주문 후 로그 출력 및 메세지 전송 요청
     *
     * @param result       주문 결과 Dto
     * @param orderTrading 매도 주문 정보 Entity
     * @param <T>          WebClientCommonResDto를 상속하는 클래스
     */
    @Override
    public <T extends WebClientCommonResDto> void orderApiResultProcess(T result, OrderTrading orderTrading) {
        if (result.isSuccess()) {
            LogUtils.tradingOrderSuccess(OVERSEAS_STOCK_SELL_ORDER, orderTrading.getItemNameKor());
            notificator.sendMessage(MessageContentDto.OrderResult.fromOne(
                    OVERSEAS_STOCK_SELL_ORDER, getAccountStatus(), orderTrading));
        } else {
            LogUtils.openApiFailedResponseMessage(OVERSEAS_STOCK_SELL_ORDER, result.getMessage(), result.getMessageCode());
        }
    }

    private MyService myServiceImpl() {
        return myServiceFactory.resolveService(MarketType.OVERSEAS);
    }

    /**
     * 금일 인자로 전달받은 itemCode에 대해 최초 매도 주문인지 확인한다.
     *
     * @param itemCode      주식 종목
     * @param transactionId 매도 transactionId
     * @param marketStart
     * @param marketEnd
     * @return 신규 주문이면
     */
    @Override
    public boolean isNewOrder(String itemCode, String transactionId, LocalDateTime marketStart, LocalDateTime marketEnd) {
        return 0 == orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                itemCode, OPENAPI_SUCCESS_RESULT_CODE, transactionId, marketStart, marketEnd);
    }
}