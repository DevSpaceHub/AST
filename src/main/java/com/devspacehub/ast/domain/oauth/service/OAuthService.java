/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : OAuthService
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth.service;

import com.devspacehub.ast.domain.oauth.service.dto.AccessTokenIssueExternalReqDto;
import com.devspacehub.ast.domain.oauth.service.dto.OAuthTokenIssueExternalResDto;
import com.devspacehub.ast.util.OpenApiCall;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * OpenApi 호출 - OAuth 서비스.
 */
@Service
@RequiredArgsConstructor
public class OAuthService {
    private final OpenApiCall openApiCall;
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
    public String issueAccessToken() {
        String requestUri = "/oauth2/tokenP";
        AccessTokenIssueExternalReqDto dto = AccessTokenIssueExternalReqDto.builder()
                .appkey(appKey)
                .appsecret(appSecret)
                .build();
        String response = openApiCall.httpOAuthRequest(requestUri, dto);
        OAuthTokenIssueExternalResDto.WebClient resDto = null;
        try {

            resDto = objectMapper.readValue(
                    response != null ? response : null,
                    OAuthTokenIssueExternalResDto.WebClient.class);
        } catch (Exception e) {
            System.out.println("error");
        }
        return resDto.getAccess_token();
    }
}
