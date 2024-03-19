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
import com.devspacehub.ast.domain.my.stockBalance.service.MyService;
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
     * 예수금의 00 퍼센트와 주문가를 이용여 알고리즘을 이용해 매수 수량 구한다.
     * @param myCash 예수금
     * @param calculatedOrderPrice 주문가
     * @return 소수점 버려진 int 타입의 매수 수량
     */
    public int calculateOrderQuantity(int myCash, int calculatedOrderPrice) {
        Float orderQuantity = ((myCash * NumberUtil.percentageToDecimal(cashBuyOrderAmountPercent)) / splitBuyCount) / calculatedOrderPrice;
        return orderQuantity.intValue();
    }

    /**
     * 계산된 수량 값이 0인지 체크한다.
     * @param orderQuantity 주문 수량
     * @return 수량이 0이면 True 반환. 아니면 False.
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
     * @param resDto 거래량 순위 종목 조회 DTO
     * @return 매수 주문할 종목들의 List 타입
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

            OpenApiRequest.timeDelay();
            // 6. 매수 금액 + 매수 수량 결정 (분할 매수 Case)
            SplitBuyPercents splitBuyPercents = SplitBuyPercents.of(splitBuyPercentsByComma);

            for (int idx = 0; idx < splitBuyPercents.getPercents().size(); idx++) {
                int orderPriceByPriceUnit = orderPriceCuttingByPriceUnit(
                        splitBuyPercents.calculateOrderPriceBySplitBuyPercents(currentPrice, idx),
                        StockPriceUnit.getPriceUnitBy(currentPrice));
                int orderQuantity = calculateOrderQuantity(myDeposit, orderPriceByPriceUnit);

                if (isZero(orderQuantity)) {
                    log.info("[buy] 매수 주문 금액이 부족.(종목명: {}, 예수금: {})", stockInfo.getHtsStockNameKor(), myDeposit);
                    continue;
                }

                log.info("[buy] 주문 수량 : {}, 주문가: {} (분할 매수 퍼센트: {})", orderQuantity, orderPriceByPriceUnit,
                        splitBuyPercents.getPercents().get(idx));
                pickedStockItems.add(StockItemDto.from(stockInfo, orderQuantity, orderPriceByPriceUnit));
            }
        }
        return pickedStockItems;
    }


    /**
     * 호가 단위에 따라 주문가 조정하여 반환한다.
     * @param calculatedOrderPrice 주문가
     * @return int 타입의 주문가
     */
    protected int orderPriceCuttingByPriceUnit(int calculatedOrderPrice, int priceUnit) {
        Float decimal = calculatedOrderPrice / (float) priceUnit;
        return decimal.intValue() * priceUnit;
    }

    /**
     * 매수 가능한지 체크한다.
     * @param stockInfo 종목 정보 DTO
     * @return 유효한 종목이고 신규 주문이면 True 반환한다. 반대는 False.
     */
    protected boolean isStockItemBuyOrderable(StockInfo stockInfo) {
        if (itemInfoRepository.countByItemCode(stockInfo.getStockCode()) < 1) {
            return false;
        }
        return isNewOrder(stockInfo.getStockCode());
    }

    /**
     * 종목에 대해 새 주문인지 체크
     * @param stockCode String 타입의 종목 코드
     * @return 금일 기준 신규 주문이면 True 반환. 이미 주문 이력 있으면 False 반환.
     */
    public boolean isNewOrder(String stockCode){
        return 0 == orderTradingRepository.countByItemCodeAndOrderResultCodeAndTransactionIdAndRegistrationDateTimeBetween(
                stockCode, OPENAPI_SUCCESS_RESULT_CODE, txIdBuyOrder,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59)));
    }

    /**
     * 지표(per, pbr, 투자유의여부(N), 단기과열여부(N), 정리매매여부(N), 시가총액, 거래량) 고려
     * @param currentStockPriceInfo 현재가 시세 조회 응답 DTO
     * @return 매수 지표 기준에 부합하면 True 반환. 부합하지 않으면 False 반환.
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

    /**
     * 매수 주문 후 이력 테이블에 저장.
     * @param orderTradingInfos 매수 주문 성공 정보
     */
    @Transactional
    @Override
    public void saveInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            orderTradingRepository.saveAll(orderTradingInfos);
        }
    }
}