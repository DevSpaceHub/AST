/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OpenApiRequestTest
 creation : 2024.6.4
 author : Yoonji Moon
 */

package com.devspacehub.ast.util;

import com.devspacehub.ast.common.config.WebClientConfig;
import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.domain.marketStatus.dto.OverseasStockConditionSearchResDto;
import com.devspacehub.ast.domain.orderTrading.dto.StockOrderApiResDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@Import(WebClientConfig.class)
class OpenApiRequestTest {

    @Spy
    private ObjectMapper objectMapper;

    @DisplayName("ObjectMappe가 역직렬화 시, 하위 클래스로 타입을 지정하면 역직렬화할 수 있다.")
    @Test
    void deserializeTest() throws Exception {
        // given
        String apiJsonResponse = "{\"rt_cd\": \"0\",\"msg_cd\": \"APBK0013\",\"msg1\": \"주문 전송 완료 되었습니다.\",\"output\":{\"KRX_FWDG_ORD_ORGNO\":\"06010\",\"ODNO\":\"0001569157\",\"ORD_TMD\": \"155211\"}}";
        // when
        StockOrderApiResDto response = objectMapper.readValue(apiJsonResponse, StockOrderApiResDto.class);
        // then
        assertNotNull(response);
        assertThat(response.getMessageCode()).isEqualTo("APBK0013");
        assertThat(response.getResultCode()).isEqualTo("0");
        assertThat(response.getOutput().getOrderTime()).isEqualTo("155211");
    }
    @DisplayName("해외주식 조건검색 API 응답 Json 데이터를 ObjectMapper가 역직렬화 시, DTO 내 Enum 클래스 필드로 자동 전환된다.")
    @Test
    void deserializeTestWithEnumClassField() throws Exception {
        // given
        String apiJsonResponse = "{\n" +
                "    \"output1\": {\n" +
                "        \"zdiv\": \"4\",\n" +
                "        \"stat\": \"무료실시간\",\n" +
                "        \"crec\": \"38\",\n" +
                "        \"trec\": \"38\",\n" +
                "        \"nrec\": \"38\"\n" +
                "    },\n" +
                "    \"output2\": [\n" +
                "        {\n" +
                "            \"rsym\": \"DNASTSLA\",\n" +
                "            \"excd\": \"NAS\",\n" +
                "            \"symb\": \"TSLA\",\n" +
                "            \"name\": \"테슬라\",\n" +
                "            \"last\": \"178.0800\",\n" +
                "            \"sign\": \"5\",\n" +
                "            \"diff\": \"0.7100\",\n" +
                "            \"rate\": \"-0.40\",\n" +
                "            \"tvol\": \"67314602\",\n" +
                "            \"popen\": \"178.5000\",\n" +
                "            \"phigh\": \"180.3200\",\n" +
                "            \"plow\": \"173.8200\",\n" +
                "            \"valx\": \"567932736\",\n" +
                "            \"shar\": \"3189200000\",\n" +
                "            \"avol\": \"11932027130\",\n" +
                "            \"eps\": \"3.91\",\n" +
                "            \"per\": \"45.51\",\n" +
                "            \"rank\": \"9\",\n" +
                "            \"ename\": \"TESLA INC\",\n" +
                "            \"e_ordyn\": \"○\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"rt_cd\": \"0\",\n" +
                "    \"msg_cd\": \"MCA00000\",\n" +
                "    \"msg1\": \"정상처리 되었습니다.\"\n" +
                "}";

        // when
        OverseasStockConditionSearchResDto response = objectMapper.readValue(apiJsonResponse, OverseasStockConditionSearchResDto.class);
        // then
        assertNotNull(response);
        assertThat(response.getMessageCode()).isEqualTo("MCA00000");
        assertThat(response.getOutput().getNrec()).isEqualTo("38");
        assertThat(response.getResultDetails().get(0).getCurrentPrice()).isEqualTo(new BigDecimal("178.0800"));
        assertThat(response.getResultDetails().get(0).getExchangeCode()).isEqualTo(ExchangeCode.NASDAQ);
    }

}