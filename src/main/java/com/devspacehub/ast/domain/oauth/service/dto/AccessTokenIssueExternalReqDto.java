/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : AccessTokenIssueExternalDto
 creation : 2023.12.16
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth.service.dto;

import com.devspacehub.ast.common.dto.WebClientRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * OpenApi 호출 - 접근 토큰 발급 Request DTO.
 */
@ToString
@NoArgsConstructor
@Getter
public class AccessTokenIssueExternalReqDto /*extends WebClientRequestDto*/ {

    private String grant_type;
    private String appkey;
    private String appsecret;

    /**
     * Instantiates a new Access token issue external req dto.
     *
     * @param appkey    the appkey
     * @param appsecret the appsecret
     */
    @JsonIgnore
    @Builder
    public AccessTokenIssueExternalReqDto(String appkey, String appsecret) {
        super();
        this.grant_type = "client_credentials";
        this.appkey = appkey;
        this.appsecret = appsecret;
    }
    /*@JsonIgnore
    @Override
    public WebClientRequestDto getBody() {
        return (AccessTokenIssueExternalReqDto) this;
    }

    @JsonIgnore
    @Override
    public Consumer<HttpHeaders> getHeaders() {
        return httpHeaders -> {
        };
    }*/
}
