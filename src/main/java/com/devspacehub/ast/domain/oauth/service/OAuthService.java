/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OAuthService
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth.service;

import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.oauth.OAuthTokens;
import com.devspacehub.ast.domain.oauth.OAuthRepository;
import com.devspacehub.ast.domain.oauth.dto.AccessTokenIssueExternalReqDto;
import com.devspacehub.ast.domain.oauth.dto.OAuthTokenIssueExternalResDto;
import com.devspacehub.ast.exception.error.DtoConversionException;
import com.devspacehub.ast.openApiUtil.OpenApiRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.devspacehub.ast.common.constant.OpenApiType.OAUTH_ACCESS_TOKEN_ISSUE;

/**
 * OpenApi 호출 - OAuth 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {
    private final OpenApiRequest openApiRequest;
    private final OAuthRepository oAuthRepository;
    private final ObjectMapper objectMapper;
    @Value("${openapi.rest.appkey}")
    private String appKey;

    @Value("${openapi.rest.appsecret}")
    private String appSecret;

    /**
     * Issue access token string.
     *
     * @return the string
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String issueAccessToken(TokenType requiredTokenType) {
        // 이미 발급해놓은 token 있으면 사용
        Optional<OAuthTokens> oauth = oAuthRepository.findTopByTokenTypeIsAndOauthTokenExpiredGreaterThanOrderByRegistrationDatetimeDesc(requiredTokenType, LocalDateTime.now());
        if (oauth.isPresent()) {
            return oauth.get().getOauthToken();
        }

        AccessTokenIssueExternalReqDto dto = AccessTokenIssueExternalReqDto.builder()
                .grantType("client_credentials")
                .appKey(appKey)
                .appSecret(appSecret)
                .build();
        String response = openApiRequest.httpOAuthRequest(OAUTH_ACCESS_TOKEN_ISSUE.getUri(), dto);
        OAuthTokenIssueExternalResDto.WebClient resDto = null;
        try {
            resDto = objectMapper.readValue(
                    response != null ? response : null,
                    OAuthTokenIssueExternalResDto.WebClient.class);
        } catch (Exception e) {
            throw new DtoConversionException();
        }

        OAuthTokens savedToken = oAuthRepository.save(resDto.toEntity());

        return savedToken.getOauthToken();
    }
}
