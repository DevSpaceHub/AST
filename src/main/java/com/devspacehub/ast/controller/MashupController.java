/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : MashupController
 creation : 2023.12.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller;

import com.devspacehub.ast.domain.mashup.service.MashupServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AST-BATCH에서 호출하는 매쉬업 컨트롤러
 * - 해당 컨트롤러에서 비즈니스 로직 시작됨.
 */
@RestController
@RequiredArgsConstructor
public class MashupController {
    private final MashupServiceImpl mashupService;

    /**
     * Mashup response entity.
     *
     * @return the response entity
     */
    @PostMapping("/first")
    public ResponseEntity<Void> mashup() {
        mashupService.startTrading();
        return ResponseEntity.ok().build();
    }
}
