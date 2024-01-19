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
import com.devspacehub.ast.exception.error.BusinessException;
import com.devspacehub.ast.exception.error.DtoConversionException;
import com.devspacehub.ast.exception.error.ErrorCode;
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
    private final OpenApiProperties openApiProperties;
    private final OAuthRepository oAuthRepository;
    private final ObjectMapper objectMapper;
    private final OAuthService oAuthService;

    @Value("${openapi.rest.appkey}")
    private String appKey;

    @Value("${openapi.rest.appsecret}")
    private String appSecret;

    /**
     * RESTful 접근 토큰 발급
     * @return the string
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // TODO api 분리되면 전파옵션 제거 가능
    public void issueAccessToken() {
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
        oAuthRepository.save(resDto.toEntity());
    }

    /**
     * 발급해놓은 token 사용
     *
     * @return the access token
     */
    public void setAccessToken(TokenType requiredTokenType) {
        Optional<OAuthTokens> oauth = oAuthRepository.findTopByTokenTypeIsAndOauthTokenExpiredGreaterThanOrderByRegistrationDatetimeDesc(requiredTokenType, LocalDateTime.now());
        if (oauth.isPresent()) {
            openApiProperties.setOauth(oauth.get().getOauthToken());
            return;
        } else {
            // TODO 추후 제거할 temp 로직
            oAuthService.issueAccessToken();
            openApiProperties.setOauth(oAuthRepository.findTopByTokenTypeIsAndOauthTokenExpiredGreaterThanOrderByRegistrationDatetimeDesc(requiredTokenType, LocalDateTime.now()).get().getOauthToken());
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ACCESS_TOKEN);
    }
}
