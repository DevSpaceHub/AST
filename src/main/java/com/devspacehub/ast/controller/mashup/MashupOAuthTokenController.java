/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MashupOAuthTokenController
 creation : 2024.1.17
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller.mashup;

import com.devspacehub.ast.common.dto.ApiResult;
import com.devspacehub.ast.domain.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AST-BATCH에서 호출하는 매쉬업 컨트롤러 - 접근 토큰 발급
 */
@RestController
@RequestMapping("/ast/token")
@RequiredArgsConstructor
public class MashupOAuthTokenController {
    private final OAuthService oAuthService;

    /**
     * Oauth token issue response entity.
     *
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<ApiResult> oauthTokenIssue() {
        oAuthService.issueAccessToken();
        return ResponseEntity.ok(ApiResult.success());
    }
}
