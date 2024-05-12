/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MashupConclusionControllerTest
 creation : 2024.5.7
 author : Yoonji Moon
 */

package com.devspacehub.ast.controller.mashup;

import com.devspacehub.ast.domain.mashup.service.MashupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MashupConclusionController 테스트코드
 */
@WebMvcTest(MashupConclusionController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MashupConclusionControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MashupService mashupService;

    @DisplayName("금일의 체결 결과를 조회하여 후처리한다.")
    @Test
    void conclusionResultProcess() throws Exception {
        mockMvc.perform(
                put("/ast/conclusions/process")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.time").exists())
                .andExpect(jsonPath("$.code").exists())
                .andDo(print());

        verify(mashupService).startOrderConclusionResultProcess();
    }
}