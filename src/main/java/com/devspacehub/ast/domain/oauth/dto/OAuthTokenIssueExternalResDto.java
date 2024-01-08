/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : AccessTokenIssueExternalResDto
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth.dto;

import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.oauth.OAuthTokens;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * OpenApi - OAuth 요청 res dto.
 */
@NoArgsConstructor
@Getter
public abstract class OAuthTokenIssueExternalResDto {
    /**
     * The type Web client.
     * {
     * "access_token":"","access_token_token_expired":"2023-12-28 21:07:08","token_type":"Bearer","expires_in":86400}
     */
    @NoArgsConstructor
    @Setter
    @Getter
    public static class WebClient extends OAuthTokenIssueExternalResDto {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private int expiresInPerSec;
        @JsonProperty("access_token_token_expired")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime accessTokenExpired;   //2023-12-30 06:52:04

        public OAuthTokens toEntity() {
            return OAuthTokens.builder()
                    .oauthToken(accessToken)
                    .oauthTokenExpired(accessTokenExpired)
                    .registrationDatetime(LocalDateTime.now())
                    .registrationId("application")
                    .tokenType(TokenType.AccessToken)
                    .build();
        }
    }

    /**
     * The type Web socket.
     */
    public static class WebSocket extends OAuthTokenIssueExternalResDto {

        @JsonProperty("approval_key")
        private String approvalKey;
    }

}
