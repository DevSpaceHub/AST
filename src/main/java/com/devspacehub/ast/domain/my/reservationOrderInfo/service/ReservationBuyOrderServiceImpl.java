/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationBuyOrderServiceImpl
 creation : 2024.3.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.StockPriceUnit;
import com.devspacehub.ast.common.utils.LogUtils;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
import com.devspacehub.ast.domain.my.stockBalance.service.MyService;
import com.devspacehub.ast.domain.notification.Notificator;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.OrderTradingRepository;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalReqDto;
import com.devspacehub.ast.domain.orderTrading.dto.DomesticStockOrderExternalResDto;
import com.devspacehub.ast.domain.orderTrading.service.TradingService;
import com.devspacehub.ast.util.EnvironmentUtil;
import com.devspacehub.ast.util.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.devspacehub.ast.common.constant.CommonConstants.ORDER_DIVISION;
import static com.devspacehub.ast.common.constant.OpenApiType.DOMESTIC_STOCK_RESERVATION_BUY_ORDER;
import static com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto.*;

/**
 * 주문 서비스 - 예약 매수
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReservationBuyOrderServiceImpl extends TradingService {
    private final OpenApiRequest openApiRequest;
    private final ReservationOrderInfoRepository reservationOrderInfoRepo;
    private final OrderTradingRepository orderTradingRepository;
    private final MarketStatusService marketStatusService;
    private final MyService myService;
    private final Notificator notificator;

    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType, String transactionId) {
        // 1. 예약 종목들 조회
        List<ReservationOrderInfo> reservationOrderInfos = reservationOrderInfoRepo
                .findAllByOrderStartDateBeforeOrOrderStartDateEqualsAndOrderEndDateAfterOrOrderEndDateEqualsOrderByPriority(
                        LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now());

        // 2. 매수 종목 선택 및 주문
        List<OrderTrading> orderTradings = new ArrayList<>();
        for (StockItemDto item : pickStockItems(reservationOrderInfos)) {
            DomesticStockOrderExternalResDto result = callOrderApi(openApiProperties, item, DOMESTIC_STOCK_RESERVATION_BUY_ORDER, transactionId);
            OrderTrading orderTrading = OrderTrading.from(item, result, transactionId);
            orderTradings.add(orderTrading);

            orderApiResultProcess(result, orderTrading);
        }

        return orderTradings;
    }

    /**
     * 매수 주문할 종목들 선택
     * - 현재가 시세 조회
     * - 호가 단위에 맞게 조정
     * - 충분한 예수금 있는지, 하한가보다 높은지 체크
     * @param reservationOrderInfos
     */
    public List<StockItemDto> pickStockItems(List<ReservationOrderInfo> reservationOrderInfos) {
        Map<Long, ReservationOrderInfo> itemCodeReservationOrderInfoMap = reservationOrderInfos.stream()
                .collect(Collectors.toMap(ReservationOrderInfo::getSeq, stock -> stock));

        // 현재가 시세 조회 API
        Map<Long, CurrentStockPriceInfo> itemCodeResponseMap = reservationOrderInfos.stream()
                .collect(Collectors.toMap(ReservationOrderInfo::getSeq, orderInfo -> {
                    return marketStatusService.getCurrentStockPrice(orderInfo.getItemCode());
                }));

        List<StockItemDto> pickedStockItems = new ArrayList<>();
        for (Long seq : itemCodeReservationOrderInfoMap.keySet()) {
            ReservationOrderInfo currReservationOrderInfo = itemCodeReservationOrderInfoMap.get(seq);

            // 호가 단위 조정
            int adjustedOrderPrice = StockPriceUnit.orderPriceCuttingByPriceUnit(currReservationOrderInfo.getOrderPrice());
            currReservationOrderInfo.updateToAdjustedPrice(adjustedOrderPrice);

            // 예수금 체크
            int myDeposit = myService.getBuyOrderPossibleCash(currReservationOrderInfo.getItemCode(), adjustedOrderPrice, ORDER_DIVISION);
            if (myService.isMyDepositLowerThanOrderPrice(myDeposit, adjustedOrderPrice * currReservationOrderInfo.getOrderQuantity())) {
                LogUtils.insufficientAmountError(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, currReservationOrderInfo.getKoreanItemName(), myDeposit);
                continue;
            }
            // 하한가 비교
            int responseLowerLimitPrice = Integer.parseInt(itemCodeResponseMap.get(seq).getStockLowerLimitPrice());
            if (currReservationOrderInfo.isOrderPriceGreaterOrEqualThan(responseLowerLimitPrice)) {
                pickedStockItems.add(StockItemDto.of(currReservationOrderInfo));
            }
        }
        log.info("[예약매수 주문] 최종 선택된 주식 종목 갯수 : {}", pickedStockItems.size());
        return pickedStockItems;
    }

    @Override
    public DomesticStockOrderExternalResDto callOrderApi(OpenApiProperties openApiProperties, StockItemDto stockItem, OpenApiType openApiType, String transactionId) {
        Consumer<HttpHeaders> httpHeaders = DomesticStockOrderExternalReqDto.setHeaders(openApiProperties.getOauth(), transactionId);
        DomesticStockOrderExternalReqDto bodyDto = DomesticStockOrderExternalReqDto.from(openApiProperties, stockItem);

        return (DomesticStockOrderExternalResDto) openApiRequest.httpPostRequest(openApiType, httpHeaders, bodyDto);
    }

    @Override
    @Transactional
    public void saveInfos(List<OrderTrading> orderTradingInfos) {
        if (!orderTradingInfos.isEmpty()) {
            orderTradingRepository.saveAll(orderTradingInfos);
        }
    }

    @Override
    public void orderApiResultProcess(DomesticStockOrderExternalResDto result, OrderTrading orderTrading) {
        if (result.isSuccess()) {
            LogUtils.tradingOrderSuccess(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, orderTrading.getItemNameKor());
            notificator.sendMessage(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, EnvironmentUtil.getActiveProfile(), orderTrading);
        } else {
            LogUtils.openApiFailedResponseMessage(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, result.getMessage(), result.getMessageCode());
        }
    }
}
