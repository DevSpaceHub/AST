/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MashupOAuthTokenControllerTest
 creation : 2024.2.11
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller.mashup;

import com.devspacehub.ast.domain.oauth.service.OAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MashupOAuthTokenController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MashupOAuthTokenControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    OAuthService oAuthService;

    @Test
    @DisplayName("접근 토큰 발급 컨트롤러 테스트")
    void oauthTokenIssue() throws Exception {
        mockMvc.perform(
                post("/ast/token")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.code").exists())
                .andDo(print());

        verify(oAuthService).issueAccessToken();
    }
}