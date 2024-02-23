/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyOrderServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.StockPriceUnit;
import com.devspacehub.ast.common.dto.WebClientCommonResDto;
import com.devspacehub.ast.domain.itemInfo.ItemInfoRepository;
import com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto.CurrentStockPriceInfo;
import com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.service.MyService;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.domain.orderTrading.dto.SplitBuyPercents;
import com.devspacehub.ast.util.NumberUtil;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.CommonConstants.OPENAPI_SUCCESS_RESULT_CODE;
import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_BUY_ORDER;
import static com.devspacehub.ast.common.constant.YesNoStatus.YES;
import static com.devspacehub.ast.domain.marketStatus.dto.DomStockTradingVolumeRankingExternalResDto.*;

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
    private final MarketStatusService marketStatusService;
    private final ItemInfoRepository itemInfoRepository;

    @Value("${openapi.rest.header.transaction-id.buy-order}")
    private String txIdBuyOrder;

    @Value("${trading.limit-price-to-book-ratio}")
    private float limitPBR;
    @Value("${trading.limit-price-earnings-ratio}")
    private float limitPER;
    @Value("${trading.limit-hts-market-capital}")
    private Long limitHtsMarketCapital;
    @Value("${trading.limit-accumulation-volume}")
    private Integer limitAccumulationVolume;
    @Value("${trading.cash-buy-order-amount-percent}")
    private int cashBuyOrderAmountPercent;
    @Value("${trading.split-buy-percents-by-comma}")
    private String splitBuyPercentsByComma;
    @Value("${trading.split-buy-count}")
    private int splitBuyCount;
    private static final long TIME_DELAY_MILLIS = 200L;
    private static final String COMMA = ",";


    /**
     * 국내주식 매수 주문
     * @param stockItem
     * : stockCode 종목코드(6자리) / orderDivision 주문구분(지정가,00) / orderQuantity 주문수량 / orderPrice 주문단가
     */
    @Override
    public DomesticStockOrderExternalResDto order(StockItemDto stockItem) {
        // 매수 주문
        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyOrder);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(stockItem.getStockCode())
                .orderDivision(stockItem.getOrderDivision())
                .orderQuantity(String.valueOf(stockItem.getOrderQuantity()))
                .orderPrice(String.valueOf(stockItem.getOrderPrice()))
                .build();

        return (DomesticStockOrderExternalResDto) openApiRequest.httpPostRequest(DOMESTIC_STOCK_BUY_ORDER, httpHeaders, bodyDto);
    }

    /**
     * 매수 수량 구하기
     * 소수점 버림.
     * @param myCash
     * @param calculatedOrderPrice
     * @return
     */
    public int calculateOrderQuantity(int myCash, int calculatedOrderPrice) {
        Float orderQuantity = ((myCash * NumberUtil.percentageToDecimal(cashBuyOrderAmountPercent)) / splitBuyCount) / calculatedOrderPrice;
        return orderQuantity.intValue();
    }

    /**
     * 매수 수량이 0이면 매수 불가능.
     */
    public boolean isZero(int orderQuantity) {
        return orderQuantity == 0;
    }

    private boolean isStockMarketClosed(String messageCode) {
        return "40100000".equals(messageCode);
    }

    /**
     * 알고리즘에 따라 매수할 종목 선택
     * 1. 거래량 순위 종목 조회하여 상위 10개 순회
     * 2. valid check : table에 없는 종목 매수 X (파생상품)
     * 3. 현재가 시세 조회
     * 4. 지표 체크
     * 5. 매수 가능 현금 조회
     * - 주문 단가 결정 (호가 단위 고려)
     * - 매수 수량 결정
     * - 매수 가능 여부 확인
     * 6. 분할 매수
     * @param resDto
     * @return
     */
    public List<StockItemDto> pickStockItems(WebClientCommonResDto resDto) {
        // 1. 거래량 순위 종목 조회
        DomStockTradingVolumeRankingExternalResDto stockItems = (DomStockTradingVolumeRankingExternalResDto) resDto;

        List<StockItemDto> pickedStockItems = new ArrayList<>();

        int count = 0;
        while (count++ < 10) {
            StockInfo stockInfo = stockItems.getStockInfos().get(count);
            // 2. 매수 가능 여부 체크
            if (!isStockItemBuyOrderable(stockInfo)) {
                continue;
            }

            // 3. 현재가 시세 조회
            CurrentStockPriceInfo currentStockPriceInfo = marketStatusService.getCurrentStockPrice(stockInfo.getStockCode()).getCurrentStockPriceInfo();
            int currentPrice = Integer.parseInt(currentStockPriceInfo.getCurrentStockPrice());

            log.info("[buy] 종목: {}({})", stockInfo.getStockCode(), stockInfo.getHtsStockNameKor());
            log.info("[buy] 현재가: {}", currentPrice);
            log.info("[buy] HTS 시가 총액: {}", currentStockPriceInfo.getHtsMarketCapitalization());
            log.info("[buy] 누적 거래량: {}", currentStockPriceInfo.getAccumulationVolume());
            log.info("[buy] PER: {}", Objects.isNull(currentStockPriceInfo.getPer()) ? "Null" : currentStockPriceInfo.getPer());
            log.info("[buy] PBR: {}", Objects.isNull(currentStockPriceInfo.getPbr()) ? "Null" : currentStockPriceInfo.getPbr());
            log.info("[buy] 투자유의 여부: {}", currentStockPriceInfo.getInvtCarefulYn());
            log.info("[buy] 정리매매 여부: {}", currentStockPriceInfo.getDelistingYn());
            log.info("[buy] 단기과열 여부: {}", currentStockPriceInfo.getShortOverYn());

            // 4. 지표 체크
            if (!checkAccordingWithIndicators(currentStockPriceInfo)) {
                continue;
            }
            // 5. 매수 가능 금액 조회
            int myDeposit = myService.getBuyOrderPossibleCash(stockInfo.getStockCode(), currentPrice, ORDER_DIVISION);

            timeDelay();
            // 6. 매수 금액 + 매수 수량 결정 (분할 매수 Case)
            Float[] percents = Arrays.stream(splitBuyPercentsByComma.split(COMMA))
                    .map(percent -> NumberUtil.percentageToDecimal(Integer.parseInt(percent))).toArray(Float[]::new);
            SplitBuyPercents splitBuyPercents = new SplitBuyPercents(percents);

            for (int idx = 0; idx < splitBuyPercents.getPercents().size(); idx++) {
                Float calculatedOrderPrice = splitBuyPercents.calculateBuyPriceBySplitBuyPercents(currentPrice, idx);

                int orderQuantity = calculateOrderQuantity(myDeposit, orderPriceCuttingByPriceUnit(calculatedOrderPrice, StockPriceUnit.getPriceUnitBy(currentPrice)));
                log.info("[buy] 주문 수량 : {}, 주문 가격: {} ({})", orderQuantity, calculatedOrderPrice, splitBuyPercents.getPercents().get(idx));

                if (isZero(orderQuantity)) {
                    log.info("[buy] 매수 주문 금액이 부족.(종목명: {}, 예수금: {})", stockInfo.getHtsStockNameKor(), myDeposit);
                    continue;
                }
                pickedStockItems.add(StockItemDto.from(stockInfo, orderQuantity, calculatedOrderPrice.intValue()));
            }
        }
        return pickedStockItems;
    }


    /**
     * 호가 단위에 따라 주문가 조정
     * @param calculatedOrderPrice
     * @return
     */
    protected int orderPriceCuttingByPriceUnit(Float calculatedOrderPrice, int priceUnit) {
        Float decimal = calculatedOrderPrice / priceUnit;
        return decimal.intValue() * priceUnit;
    }

    /**
     * 매수 가능한지 체크
     * @param stockInfo
     * @return
     */
    private boolean isStockItemBuyOrderable(StockInfo stockInfo) {
        if (1 > itemInfoRepository.countByItemCode(stockInfo.getStockCode())) {
            return false;
        }
        if (itemInfoRepository.countByItemCode(stockInfo.getStockCode()) < 1) {
            return false;
        }
        return isNewOrder(stockInfo.getStockCode());
    }

    /**
     * 종목에 대해 새 주문인지 체크
     * @param stockCode
     * @return
     */
    public boolean isNewOrder(String stockCode){
        return 0 == orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                stockCode, OPENAPI_SUCCESS_RESULT_CODE, txIdBuyOrder,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8,59,0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15,0,0)));
    }
    /**
     * KIS Open API를 초당 2회 이상 호출하지 않기 위해 시간 지연 수행.
     */
    private void timeDelay() {
        try {
            Thread.sleep(TIME_DELAY_MILLIS);
        } catch (InterruptedException ex) {
            log.error("시간 지연 처리 중 이슈 발생하였습니다.");
            log.error("{}", ex.getStackTrace());
        }
    }

    /**
     * 지표(per, pbr, 투자유의여부(N), 단기과열여부(N), 정리매매여부(N), 시가총액, 거래량) 고려
     * @param currentStockPriceInfo
     * @return
     */
    protected boolean checkAccordingWithIndicators(CurrentStockPriceInfo currentStockPriceInfo) {
        if (YES.getCode().equals(currentStockPriceInfo.getInvtCarefulYn()) ||
                YES.getCode().equals(currentStockPriceInfo.getShortOverYn()) ||
                YES.getCode().equals(currentStockPriceInfo.getDelistingYn())) {
            return false;
        }
        if (Strings.isEmpty(currentStockPriceInfo.getPer()) || Strings.isEmpty(currentStockPriceInfo.getPbr())) {
            return false;
        }
        if (Float.parseFloat(currentStockPriceInfo.getPer()) > limitPER || Float.parseFloat(currentStockPriceInfo.getPbr()) > limitPBR) {
            return false;
        }
        if (Long.parseLong(currentStockPriceInfo.getHtsMarketCapitalization()) < limitHtsMarketCapital) { // 시가총액 (3000억 이상이어야함)
            return false;
        }
        if(Integer.parseInt(currentStockPriceInfo.getAccumulationVolume()) < limitAccumulationVolume) {   // 누적 거래량
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public void saveInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            orderTradingRepository.saveAll(orderTradingInfos);
        }
    }
}