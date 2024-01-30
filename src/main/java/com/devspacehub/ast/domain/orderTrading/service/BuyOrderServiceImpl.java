/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : BuyOrderServiceImpl
 creation : 2023.12.10
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
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
import com.devspacehub.ast.exception.error.NotEnoughCashException;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_BUY_ORDER;
import static com.devspacehub.ast.common.constant.YesNoStatus.NO;

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
    private static final long TIME_DELAY_MILLIS = 200L;


    /**
     * 국내주식 매수 주문
     * @param stockItem
     * : stockCode 종목코드(6자리) / orderDivision 주문구분(지정가,00) / orderQuantity 주문수량 / orderPrice 주문단가
     */
    @Override
    public DomesticStockOrderExternalResDto order(StockItemDto stockItem) {
        int realOrderPrice = stockItem.getCurrentStockPrice() * stockItem.getOrderQuantity();

        // 매수 가능한 금액 보유 여부 판단
        int myCash = myService.getBuyOrderPossibleCash(stockItem.getStockCode(), realOrderPrice, stockItem.getOrderDivision());
        checkBuyOrderPossible(myCash, realOrderPrice);

        // 매수 수량 결정
        String orderQuantity = calculateOrderQuantity(myCash, stockItem.getCurrentStockPrice());
        log.info("매수 수량: {}", orderQuantity);

        // 매수 주문
        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), txIdBuyOrder);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.builder()
                .accntNumber(openApiProperties.getAccntNumber())
                .accntProductCode(openApiProperties.getAccntProductCode())
                .stockCode(stockItem.getStockCode())
                .orderDivision(stockItem.getOrderDivision())
                .orderQuantity(orderQuantity)
                .orderPrice(String.valueOf(stockItem.getCurrentStockPrice()))
                .build();

        return (DomesticStockOrderExternalResDto) openApiRequest.httpPostRequest(DOMESTIC_STOCK_BUY_ORDER, httpHeaders, bodyDto);
    }

    /**
     * 매수 수량 = (매수가능 현금 % 10%) % 종목 현재가
     * 소수점 버림.
     * @param myCash
     * @param currentStockPrice
     * @return
     */
    public String calculateOrderQuantity(int myCash, Integer currentStockPrice) {
        Double orderQuantity = (myCash % 10.0) % currentStockPrice;
        return String.valueOf(orderQuantity.intValue());
    }

    /**
     * 매수 가능한 종목인지 체크
     */
    public void checkBuyOrderPossible(int myCash, int orderPrice) {
        if (orderPrice <= myCash) {
            return;
        }
        log.info("매수 주문 금액이 부족합니다. (매수 가능 금액: {})", myCash);
        throw new NotEnoughCashException();
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
        // 거래량 순위 종목
        DomStockTradingVolumeRankingExternalResDto stockItems = (DomStockTradingVolumeRankingExternalResDto) resDto;

        List<StockItemDto> pickedStockItems = new ArrayList<>();

        int count = 0;
        while (count++ < 10) {
            DomStockTradingVolumeRankingExternalResDto.StockInfo stockInfo = stockItems.getStockInfos().get(count);
            // table에 없는 종목 매수 X (파생상품)
            if (1 > itemInfoRepository.countByItemCode(stockInfo.getStockCode())) {
                continue;
            }
            // 매수 금액 결정 위해 주식 현재가 시세 조회
            CurrentStockPriceInfo currentStockPriceInfo = marketStatusService.getCurrentStockPrice(stockInfo.getStockCode()).getCurrentStockPriceInfo();
            log.info("종목코드: {}", stockInfo.getStockCode());
            log.info("현재가: {}", currentStockPriceInfo.getCurrentStockPrice());
            log.info("HTS 시가 총액: {}", currentStockPriceInfo.getHtsMarketCapitalization());
            log.info("누적 거래량: {}", currentStockPriceInfo.getAccumulationVolume());
            log.info("PER: {}", Objects.isNull(currentStockPriceInfo.getPer()) ? "Null" : currentStockPriceInfo.getPer());
            log.info("PBR: {}", Objects.isNull(currentStockPriceInfo.getPbr()) ? "Null" : currentStockPriceInfo.getPbr());
            log.info("투자유의 여부: {}", currentStockPriceInfo.getInvtCarefulYn());
            log.info("정리매매 여부: {}", currentStockPriceInfo.getDelistingYn());
            log.info("단기과열 여부: {}", currentStockPriceInfo.getShortOverYn());
            if (!checkAccordingWithIndicators(currentStockPriceInfo)) {
                continue;
            }

            pickedStockItems.add(StockItemDto.builder()
                    .stockCode(stockInfo.getStockCode())
                    .stockNameKor(stockInfo.getHtsStockNameKor())
                    .currentStockPrice(currentStockPriceInfo.getCurrentStockPrice())
                    .build());
            try {
                Thread.sleep(TIME_DELAY_MILLIS);
            } catch (InterruptedException ex) {
                log.error("시간 지연 처리 중 이슈 발생하였습니다.");
                log.error("{}", ex.getStackTrace());
            }
        }

        return pickedStockItems;
    }

    /**
     * 지표(per, pbr, 투자유의여부(N), 단기과열여부(N), 정리매매여부(N), 시가총액, 거래량) 고려
     * @param currentStockPriceInfo
     * @return
     */
    private boolean checkAccordingWithIndicators(CurrentStockPriceInfo currentStockPriceInfo) {
        if (NO.getCode().equals(currentStockPriceInfo.getInvtCarefulYn()) ||
                NO.getCode().equals(currentStockPriceInfo.getShortOverYn()) ||
                NO.getCode().equals(currentStockPriceInfo.getDelistingYn())) {
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
