/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupOrderController
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller.mashup;

import com.devspacehub.ast.common.dto.ApiResult;
import com.devspacehub.ast.domain.mashup.service.MashupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AST-BATCH에서 호출하는 매쉬업 컨트롤러 - 주문
 */
@RestController
@RequestMapping("/ast/orders")
@RequiredArgsConstructor
public class MashupOrderController {
    private final MashupService mashupService;

    /**
     * 매수 주문 API
     *
     * @return the response entity
     */
    @PostMapping("/buy")
    public ResponseEntity<ApiResult> buyOrder() {
        mashupService.startBuyOrder();
        return ResponseEntity.ok(ApiResult.success());
    }

    /**
     * 매도 주문 API
     *
     * @return the response entity
     */
    @PostMapping("/sell")
    public ResponseEntity<ApiResult> sellOrder() {
        mashupService.startSellOrder();
        return ResponseEntity.ok(ApiResult.success());
    }


    /**
     * 예약 매수 주문 API
     *
     * @return the response entity
     */
    @PostMapping("/reserve/buy")
    public ResponseEntity<ApiResult> reservationBuyOrder() {
        mashupService.startReservationBuyOrder();
        return ResponseEntity.ok(ApiResult.success());
    }



}
