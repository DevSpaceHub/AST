/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MashupConclusionController
 creation : 2024.4.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller.mashup;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.common.dto.ApiResult;
import com.devspacehub.ast.domain.mashup.service.MashupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AST-BATCH에서 호출하는 매쉬업 컨트롤러 - 체결 결과 처리
 */
@RestController
@RequestMapping("/ast/conclusions")
@RequiredArgsConstructor
public class MashupConclusionController {
    private final MashupService mashupService;

    /**
     * 체결 결과 후처리 API
     * @return Api result
     */
    @PutMapping("/process")
    public ResponseEntity<ApiResult> conclusionResultProcess(@RequestParam("openApiType") OpenApiType openApiType) {
        mashupService.startOrderConclusionResultProcess(openApiType);
        return ResponseEntity.ok(ApiResult.success());
    }
}
