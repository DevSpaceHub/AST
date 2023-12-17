/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : AccessTokenIssueExternalResDto
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OpenApi - OAuth 요청 res dto.
 */
@NoArgsConstructor
@Getter
public abstract class OAuthTokenIssueExternalResDto {
    /**
     * The type Web client.
     */
    @NoArgsConstructor
    @Getter
    public static class WebClient extends OAuthTokenIssueExternalResDto {
        private String access_token;
        private String token_type;
        private int expires_in;
    }

    /**
     * The type Web socket.
     */
    public static class WebSocket extends OAuthTokenIssueExternalResDto {
        private String approval_key;
    }

}
