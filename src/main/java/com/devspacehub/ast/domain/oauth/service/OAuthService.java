/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OAuthService
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.common.constant.TokenType;
import com.devspacehub.ast.domain.oauth.OAuthTokens;
import com.devspacehub.ast.domain.oauth.OAuthRepository;
import com.devspacehub.ast.domain.oauth.dto.AccessTokenIssueExternalReqDto;
import com.devspacehub.ast.domain.oauth.dto.OAuthTokenIssueExternalResDto;
import com.devspacehub.ast.exception.error.DtoConversionException;
import com.devspacehub.ast.common.constant.ResultCode;
import com.devspacehub.ast.exception.error.InternalServerErrorException;
import com.devspacehub.ast.util.OpenApiRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    private final OpenApiProperties openApiProperties;
    private final OAuthRepository oAuthRepository;
    private final ObjectMapper objectMapper;

    @Value("${openapi.rest.appkey}")
    private String appKey;

    @Value("${openapi.rest.appsecret}")
    private String appSecret;

    /**
     * RESTful 접근 토큰 발급
     */
    @Transactional
    public void issueAccessToken() {
        AccessTokenIssueExternalReqDto dto = AccessTokenIssueExternalReqDto.builder()
                .grantType("client_credentials")
                .appKey(appKey)
                .appSecret(appSecret)
                .build();
        String response = openApiRequest.httpOAuthRequest(OAUTH_ACCESS_TOKEN_ISSUE, dto);

        OAuthTokenIssueExternalResDto.WebClient resDto;
        try {
            resDto = objectMapper.readValue(response, OAuthTokenIssueExternalResDto.WebClient.class);
        } catch (Exception e) {
            throw new DtoConversionException();
        }
        oAuthRepository.save(resDto.toEntity());
    }


    /**
     * 접근 토큰 조회 및 사용
     *
     * @param requiredTokenType the required token type
     */
    public void setAccessToken(TokenType requiredTokenType) {
        Optional<OAuthTokens> oauth = oAuthRepository.findTopByTokenTypeIsAndOauthTokenExpiredGreaterThanOrderByRegistrationDatetimeDesc(requiredTokenType, LocalDateTime.now());
        if (oauth.isPresent()) {
            openApiProperties.setOauth(oauth.get().getOauthToken());
            return;
        }
        throw new InternalServerErrorException(ResultCode.NOT_FOUND_ACCESS_TOKEN);
    }
}
