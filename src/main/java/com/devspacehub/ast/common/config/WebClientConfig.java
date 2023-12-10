/*
 © 2023 devspacehub, Inc. All rights reserved.

 name : WebClientConfig
 creation : 2023.12.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }

}
