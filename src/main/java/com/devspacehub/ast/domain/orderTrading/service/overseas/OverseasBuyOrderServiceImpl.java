/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasBuyOrderServiceImpl
 creation : 2024.5.25
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service.overseas;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.*;
import com.devspacehub.ast.common.utils.BigDecimalUtil;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.OverseasStockConditionSearchResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.my.service.MyServiceFactory;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.*;
import com.devspacehub.ast.domain.marketStatus.service.OverseasMarketStatusService;
import com.devspacehub.ast.domain.my.dto.MyServiceRequestDto;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.exception.error.BusinessException;
import com.devspacehub.ast.exception.error.OpenApiFailedResponseException;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.OVERSEAS_MARKET_CLOSE_TIME_KST;
import static com.devspacehub.ast.common.constant.CommonConstants.OVERSEAS_MARKET_OPEN_TIME_KST;
import static com.devspacehub.ast.common.constant.DecimalScale.*;
import static com.devspacehub.ast.common.constant.OpenApiType.OVERSEAS_STOCK_BUY_ORDER;
import static com.devspacehub.ast.common.constant.ProfileType.getAccountStatus;
import static com.devspacehub.ast.domain.marketStatus.dto.OverseasStockConditionSearchResDto.*;

/**
 * 해외 주식 주문 서비스 구현체 - 매수
 */
@Slf4j
@Service
public class OverseasBuyOrderServiceImpl extends TradingService {
    private final OverseasMarketStatusService overseasMarketStatusService;

    @Value("${openapi.rest.header.transaction-id.overseas.buy-order}")
    private String transactionId;
    @Value("${trading.overseas.split-buy-percents-by-comma}")
    private String splitBuyPercentsByComma;

    @Value("${trading.overseas.split-buy-count}")
    protected BigDecimal splitBuyCount;
    @Value("${trading.overseas.cash-buy-order-amount-percent}")
    protected BigDecimal cashBuyOrderAmountPercent;

    public OverseasBuyOrderServiceImpl(OpenApiRequest openApiRequest, Notificator notificator,
                                       OrderTradingRepository orderTradingRepository, MyServiceFactory myServiceFactory,
                                       OverseasMarketStatusService overseasMarketStatusService) {
        super(openApiRequest, notificator, orderTradingRepository, myServiceFactory);
        this.overseasMarketStatusService = overseasMarketStatusService;
    }

    /**
     * 해외 주식 매수 주문한다.
     * @param openApiProperties OpenApi 호출 시 필요한 필드
     * @param openApiType OpenApi 타입
     * @return 주문 거래 정보 Entity 타입의 리스트
     */
    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType) {
        List<OrderTrading> orderTradings = new ArrayList<>();

        OrderTrading orderTrading;
        for (StockItemDto.Overseas item : pickStockItems()) {
            try {
                orderTrading = this.buy(item, openApiProperties, openApiType);

            } catch(BusinessException ex) {
                log.warn("code = {}, message = '{}'", ex.getResultCode(), ex.getMessage());
                continue;
            }
            this.orderApiResultProcess(orderTrading);
            orderTradings.add(orderTrading);
        }
        return orderTradings;
    }
    /**
     * 매수 주문하고 결과 값을 Entity로 변환하여 반환한다.
     * @param item 요청 파라미터
     * @param openApiProperties OpenApi 요청 프로퍼티
     * @param openApiType  OpenApi 요청 타입
     * @return OrderTrading 타입의 주문 데이터
     * @throws OpenApiFailedResponseException OpenApi 실패 응답인 경우
     */
    private OrderTrading buy(StockItemDto.Overseas item, OpenApiProperties openApiProperties, OpenApiType openApiType) {
        StockOrderApiResDto apiResponse = callOrderApi(openApiProperties, item, openApiType, transactionId);
        if (apiResponse.isFailed()) {
            throw new OpenApiFailedResponseException(openApiType, apiResponse.getMessage());
        }

        return OrderTrading.from(item, apiResponse, transactionId);
    }

    /**
     * 해외 매수 주문할 종목을 선택한다.
     * @implSpec
     * NASDAQ, NEWYORK 거래소에 대해 기준 지표 값(거래량, 시가총액, PER)으로 해외주식 조건검색 API를 요청하여 상위 10개 순회한다.
     * 매수가능금액조회 API를 호출하여 주문할 수 있는 종목에 대해 주문가와 수량을 결정하여 반환한다.
     * @return 거래소 별 상위 10개 중 지표에 부합하는 해외 종목 정보 (리스트)
     */
    private List<StockItemDto.Overseas> pickStockItems() {
        List<StockItemDto.Overseas> pickedStockItems = new ArrayList<>();
        for (OverseasStockConditionSearchResDto conditionSearch : overseasMarketStatusService.getStockConditionSearch()) {
            pickedStockItems.addAll(getIndicatorPassedStocksOfTop10(conditionSearch));
        }
        return pickedStockItems;
    }

    /**
     * 거래소 코드 별 10개씩 순회하며 조건에 따라 매수 주문 가능한 종목만 선별하여 반환한다.
     * @param searchResDto 해외주식 조건검색 결과 Dto
     * @return 조건에 부합한 매수 주문 가능한 종목
     */
    public List<StockItemDto.Overseas> getIndicatorPassedStocksOfTop10(OverseasStockConditionSearchResDto searchResDto) {
        int idx = -1;
        int tradeItemIterCount = Math.min(searchResDto.getResultDetails().size(), 10);

        List<StockItemDto.Overseas> buyPossibleStocks = new ArrayList<>();
        final LocalDateTime marketOpenDateTimeKST = LocalDateTime.of(LocalDate.now(), OVERSEAS_MARKET_OPEN_TIME_KST);
        final LocalDateTime marketCloseDateTimeKST = LocalDateTime.of(LocalDate.now().plusDays(1), OVERSEAS_MARKET_CLOSE_TIME_KST);

        while (++idx < tradeItemIterCount) {
            ResultDetail stockSearchDto = searchResDto.getResultDetails().get(idx);

            if (!isStockItemBuyOrderable(stockSearchDto.getItemCode(), transactionId, marketOpenDateTimeKST, marketCloseDateTimeKST)) {
                continue;
            }

            // 매수 가능 금액
            BigDecimal currentPrice = stockSearchDto.getCurrentPrice();
            BigDecimal myDeposit = myServiceImpl().getBuyOrderPossibleCash(MyServiceRequestDto.Overseas.from(
                    stockSearchDto.getItemCode(), currentPrice, stockSearchDto.getExchangeCode()));

            // 매수 금액과 수량 결정 (분할 매수 적용)
            SplitBuyPercents splitBuyPercents = SplitBuyPercents.of(splitBuyPercentsByComma);

            for (int i = 0; i < splitBuyPercents.getPercents().size(); i++) {
                // 매수가격 조정
                BigDecimal finalOrderPrice = BigDecimalUtil.setScale(
                        splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, i), getOrderPriceDecimalScale(currentPrice));
                int orderQuantity = calculateOrderQuantity(myDeposit, finalOrderPrice);

                if (isZero(orderQuantity)) {
                    LogUtils.insufficientAmountError(OVERSEAS_STOCK_BUY_ORDER, stockSearchDto.getItemNameKor(), myDeposit);
                    continue;
                }
                log.info("[{}] 주문 수량 : {}, 주문가: {} (분할 매수 퍼센트: {})", OVERSEAS_STOCK_BUY_ORDER.getDiscription(),
                        orderQuantity, finalOrderPrice, splitBuyPercents.getPercents().get(i));
                buyPossibleStocks.add(StockItemDto.Overseas.from(stockSearchDto.getItemCode(), stockSearchDto.getItemNameKor(),
                        orderQuantity, finalOrderPrice, stockSearchDto.getExchangeCode()));
            }
        }
        log.info("[{}] 최종 매수 주문 예정 갯수 : {}", OVERSEAS_STOCK_BUY_ORDER.getDiscription(), buyPossibleStocks.size());
        return buyPossibleStocks;
    }

    /**
     * 시장 타입에 따라 MyService 구현체 반환한다.
     * @return MyService 구현체
     */
    private MyService myServiceImpl() {
        return myServiceFactory.resolveService(MarketType.OVERSEAS);
    }

    /**
     * 해외 주식 주문을 위해 OpenAPI를 호출한다.
     * @param openApiProperties OpenApi 호출 시 사용하는 프로퍼티
     * @param stockItem 주식 정보 Dto
     * @param openApiType OpenApi 호출 타입
     * @param transactionId OpenApi 트랜잭션 ID
     * @return 해외 주식 주문 Open Api 응답 데이터
     */
    @Override
    public <T extends StockItemDto> StockOrderApiResDto callOrderApi(OpenApiProperties openApiProperties, T stockItem, OpenApiType openApiType, String transactionId) {
        Consumer<HttpHeaders> httpHeaders = OverseasStockOrderApiReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        OverseasStockOrderApiReqDto bodyDto = OverseasStockOrderApiReqDto.from(openApiProperties, stockItem);

        return (StockOrderApiResDto) openApiRequest.httpPostRequest(openApiType, httpHeaders, bodyDto);
    }

    /**
     * 해외 주식 주문한 결과 및 정보를 저장한다.
     * @param orderTradingInfos 다수의 주식 주문 결과 Entity
     */
    @Override
    public List<OrderTrading> saveOrderInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            return orderTradingRepository.saveAll(orderTradingInfos);
        }
        return Collections.emptyList();
    }

    /**
     * OpenAPI 매수 주문 요청의 응답에 대한 결과 처리
     * @param orderTrading 주문 정보 Entity
     */
    public void orderApiResultProcess(OrderTrading orderTrading) {
        LogUtils.tradingOrderSuccess(OVERSEAS_STOCK_BUY_ORDER, orderTrading.getItemNameKor());
        notificator.sendMessage(MessageContentDto.OrderResult.fromOne(OVERSEAS_STOCK_BUY_ORDER, getAccountStatus(), orderTrading));
    }


    /**
     * 계산된 수량 값이 0인지 체크한다.
     * @param orderQuantity 주문 수량
     * @return 수량이 0이면 True 반환. 아니면 False.
     */
    public boolean isZero(int orderQuantity) {
        return orderQuantity == 0;
    }

    /**
     * 예수금의 특정 퍼센트와 주문가를 이용여 알고리즘을 이용해 매수 수량 구한다.
     * 1. 예수금에서 cashBuyOrderAmountPercent 만큼의 비율에 해당하는 금액만 고려한다.
     * 2. 1번 결과를 분할 매수할 수량으로 나눈다.
     * 3. 2번 결과를 주문가로 나누어 최종 주문 수량을 구한다.
     * @param myCash 예수금
     * @param calculatedOrderPrice 주문가
     * @return 소수점 버려진 int 타입의 매수 수량
     */
    public int calculateOrderQuantity(BigDecimal myCash, BigDecimal calculatedOrderPrice) {
        BigDecimal xPercentOfMyCash = myCash.multiply(BigDecimalUtil.percentageToDecimal(cashBuyOrderAmountPercent));
        BigDecimal resultDividedBySplitBuyCount = BigDecimalUtil.divide(xPercentOfMyCash, splitBuyCount, FOUR.getCode());
        return BigDecimalUtil.divide(resultDividedBySplitBuyCount, calculatedOrderPrice, ZERO.getCode()).intValue();
    }
}