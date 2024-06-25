/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockBalanceApiResDtoTest
 creation : 2024.6.14
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.stockBalance.dto.response;

import com.devspacehub.ast.common.config.WebClientConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Import(WebClientConfig.class)
class StockBalanceApiResDtoTest {
    @Spy
    private ObjectMapper objectMapper;

    @DisplayName("응답으로 받은 Json 형태의 String 데이터를 StockBalanceApiResDto 타입으로 역직렬화한다.")
    @Test
    void deserializeJsonStringDataToStockBalanceApiResDto() throws JsonProcessingException {
        // given
        String givenJson = "{\n" +
                "    \"ctx_area_fk100\": \" \",\n" +
                "    \"ctx_area_nk100\": \" \",\n" +
                "    \"output1\": [\n" +
                "        {\n" +
                "            \"pdno\": \"005930\",\n" +
                "            \"prdt_name\": \"삼성전자\",\n" +
                "            \"trad_dvsn_name\": \"현금\",\n" +
                "            \"bfdy_buy_qty\": \"4\",\n" +
                "            \"bfdy_sll_qty\": \"0\",\n" +
                "            \"thdt_buyqty\": \"0\",\n" +
                "            \"thdt_sll_qty\": \"0\",\n" +
                "            \"hldg_qty\": \"25\",\n" +
                "            \"ord_psbl_qty\": \"25\",\n" +
                "            \"pchs_avg_pric\": \"76832.0000\",\n" +
                "            \"pchs_amt\": \"1920800\",\n" +
                "            \"prpr\": \"79600\",\n" +
                "            \"evlu_amt\": \"1990000\",\n" +
                "            \"evlu_pfls_amt\": \"69200\",\n" +
                "            \"evlu_pfls_rt\": \"3.60\",\n" +
                "            \"evlu_erng_rt\": \"3.60266556\",\n" +
                "            \"loan_dt\": \"\",\n" +
                "            \"loan_amt\": \"0\",\n" +
                "            \"stln_slng_chgs\": \"0\",\n" +
                "            \"expd_dt\": \"\",\n" +
                "            \"fltt_rt\": \"1.27000000\",\n" +
                "            \"bfdy_cprs_icdc\": \"1000\",\n" +
                "            \"item_mgna_rt_name\": \"20%\",\n" +
                "            \"grta_rt_name\": \"\",\n" +
                "            \"sbst_pric\": \"0\",\n" +
                "            \"stck_loan_unpr\": \"0.0000\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"output2\": [\n" +
                "        {\n" +
                "            \"dnca_tot_amt\": \"6869637\",\n" +
                "            \"nxdy_excc_amt\": \"7941165\",\n" +
                "            \"prvs_rcdl_excc_amt\": \"7941165\",\n" +
                "            \"cma_evlu_amt\": \"0\",\n" +
                "            \"bfdy_buy_amt\": \"316000\",\n" +
                "            \"thdt_buy_amt\": \"0\",\n" +
                "            \"nxdy_auto_rdpt_amt\": \"0\",\n" +
                "            \"bfdy_sll_amt\": \"1390260\",\n" +
                "            \"thdt_sll_amt\": \"0\",\n" +
                "            \"d2_auto_rdpt_amt\": \"0\",\n" +
                "            \"bfdy_tlex_amt\": \"2732\",\n" +
                "            \"thdt_tlex_amt\": \"0\",\n" +
                "            \"tot_loan_amt\": \"0\",\n" +
                "            \"scts_evlu_amt\": \"1990000\",\n" +
                "            \"tot_evlu_amt\": \"9931165\",\n" +
                "            \"nass_amt\": \"9931165\",\n" +
                "            \"fncg_gld_auto_rdpt_yn\": \"\",\n" +
                "            \"pchs_amt_smtl_amt\": \"1920800\",\n" +
                "            \"evlu_amt_smtl_amt\": \"1990000\",\n" +
                "            \"evlu_pfls_smtl_amt\": \"69200\",\n" +
                "            \"tot_stln_slng_chgs\": \"0\",\n" +
                "            \"bfdy_tot_asst_evlu_amt\": \"9906165\",\n" +
                "            \"asst_icdc_amt\": \"25000\",\n" +
                "            \"asst_icdc_erng_rt\": \"0.25236810\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"rt_cd\": \"0\",\n" +
                "    \"msg_cd\": \"20310000\",\n" +
                "    \"msg1\": \"모의투자 조회가 완료되었습니다.\"\n" +
                "}";
        // when
        StockBalanceApiResDto result = objectMapper.readValue(givenJson, StockBalanceApiResDto.class);
        // then
        assertThat(result.getMyStockBalance().get(0).getEvaluateProfitLossRate()).isEqualTo(3.60F);
        assertThat(result.getMyStockBalance().get(0).getCurrentPrice()).isEqualTo(BigDecimal.valueOf(79600));
        assertThat(result.getMyStockBalance().get(0).getHoldingQuantity()).isEqualTo(25);

    }
}