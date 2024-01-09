/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupController
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller;

import com.devspacehub.ast.domain.mashup.service.MashupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AST-BATCH에서 호출하는 매쉬업 컨트롤러
 */
@RestController
@RequestMapping("/ast/orders")
@RequiredArgsConstructor
public class MashupController {
    private final MashupService mashupService;

    /**
     * Buy order response entity.
     *
     * @return the response entity
     */
    @PostMapping("/buy")
    public ResponseEntity<Void> buyOrder() {
        mashupService.startBuyOrder();
        return ResponseEntity.ok().build();
    }

    /**
     * Sell order response entity.
     *
     * @return the response entity
     */
    @PostMapping("/sell")
    public ResponseEntity<Void> sellOrder() {
        mashupService.startSellOrder();
        return ResponseEntity.ok().build();
    }

}
