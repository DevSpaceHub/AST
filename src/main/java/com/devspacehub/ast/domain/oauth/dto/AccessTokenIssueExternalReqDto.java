/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : AccessTokenIssueExternalReqDto
 creation : 2023.12.21
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


/**
 * OpenApi 호출 - 접근 토큰 발급 Request DTO.
 * - 다른 Open Api를 수행하기 위한 권한 발급 dto로,
 */
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccessTokenIssueExternalReqDto {

    @JsonProperty("grant_type")
    private String grantType;
    @JsonProperty("appkey")
    private String appKey;
    @JsonProperty("appsecret")
    private String appSecret;

}
