/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderService
 creation : 2024.3.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.constant.StockPriceUnit;
import com.devspacehub.ast.domain.marketStatus.dto.StockItemDto;
import com.devspacehub.ast.domain.marketStatus.service.MarketStatusService;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfo;
import com.devspacehub.ast.domain.my.reservationOrderInfo.ReservationOrderInfoRepository;
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
    private final Notificator notificator;

    @Override
    public List<OrderTrading> order(OpenApiProperties openApiProperties, OpenApiType openApiType, String transactionId) {
        // 1. 예약 종목들 조회
        List<ReservationOrderInfo> reservationOrderInfos = reservationOrderInfoRepo
                .findAllByOrderStartDateBeforeOrOrderStartDateEqualsAndOrderEndDateAfterOrOrderEndDateEquals(
                        LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now());

        // 2. 매수 종목 선택 및 주문
        List<OrderTrading> orderTradings = new ArrayList<>();
        for (StockItemDto item : pickStockItems(reservationOrderInfos)) {
            DomesticStockOrderExternalResDto result = callOrderApi(openApiProperties, item, DOMESTIC_STOCK_RESERVATION_BUY_ORDER, transactionId);
            OrderTrading orderTrading = OrderTrading.from(item, result, transactionId);
            orderTradings.add(orderTrading);

            if (result.isSuccess()) {
                log.info("===== [reservation buy order] order success ({}) =====", item.getStockNameKor());
                notificator.sendMessage(DOMESTIC_STOCK_RESERVATION_BUY_ORDER, EnvironmentUtil.getActiveProfile(), orderTrading);
            }
        }

        return orderTradings;
    }

    /**
     * 매수 주문할 종목들 선택
     * - 현재가 시세 조회
     * - 호가 단위에 맞게 조정
     * @param reservationOrderInfos
     * @return
     */
    public List<StockItemDto> pickStockItems(List<ReservationOrderInfo> reservationOrderInfos) {
        Map<String, ReservationOrderInfo> itemCodeReservationOrderInfoMap = reservationOrderInfos.stream()
                .collect(Collectors.toMap(ReservationOrderInfo::getItemCode, stock -> stock));

        // 현재가 시세 조회 API
        Map<String, CurrentStockPriceInfo> itemCodeResponseMap = reservationOrderInfos.stream()
                .collect(Collectors.toMap(ReservationOrderInfo::getItemCode, orderInfo -> marketStatusService.getCurrentStockPrice(orderInfo.getItemCode())));

        List<StockItemDto> pickedStockItems = new ArrayList<>();
        // 하한가 비교
        for (String itemCode : itemCodeReservationOrderInfoMap.keySet()) {
            int responseLowerLimitPrice = Integer.parseInt(itemCodeResponseMap.get(itemCode).getStockLowerLimitPrice());
            // 호가 단위 조정
            ReservationOrderInfo currReservationOrderInfo = itemCodeReservationOrderInfoMap.get(itemCode);
            int adjustedOrderPrice = StockPriceUnit.orderPriceCuttingByPriceUnit(currReservationOrderInfo.getOrderPrice());
            currReservationOrderInfo.updateToAdjustedPrice(adjustedOrderPrice);

            if (currReservationOrderInfo.isOrderPriceGreaterOrEqualThan(responseLowerLimitPrice)) {
                pickedStockItems.add(StockItemDto.of(currReservationOrderInfo));
            }
        }
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
}
