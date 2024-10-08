/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : WebClientConfig
 creation : 2023.12.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 설정 클래스.
 */
@Configuration
public class WebClientConfig {

    @Value("${openapi.rest.domain}")
    private String openApiDomain;

    @Value("${openapi.rest.appkey}")
    private String appKey;

    @Value("${openapi.rest.appsecret}")
    private String appSecret;

    /**
     * Web client 의 기본 공통 정보를 세팅한다.
     *
     * @return baseUrl, appKey, appsecret이 모두 세팅된 WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(openApiDomain)
                .defaultHeader("appkey", appKey)
                .defaultHeader("appsecret", appSecret)
                .build();
    }

    /**
     * Object mapper object mapper.
     *
     * @return the object mapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}