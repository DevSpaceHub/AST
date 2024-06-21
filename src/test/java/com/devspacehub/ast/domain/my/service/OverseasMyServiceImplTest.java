/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OverseasMyServiceImplTest
 creation : 2024.6.14
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.service;

import com.devspacehub.ast.common.config.OpenApiProperties;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
import com.devspacehub.ast.util.OpenApiRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

// TODO pass...
@ExtendWith(MockitoExtension.class)
class OverseasMyServiceImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WebClient webClient;

    @Spy
    OpenApiProperties openApiProperties;
    @Mock
    OpenApiRequest openApiRequest;
    @InjectMocks
    private OverseasMyServiceImpl overseasMyService;

    @Value("${openapi.rest.transaction-id.overseas.stock-balance-find}")
    private String stockBalanceFindTxId;
    @DisplayName("평가손익률, 보유 수량, 매입 금액, 현재가를 포함한 해외 주식 잔고를 조회한다.")
    @Test
    void getMyStockBalance_blog() {
        // TODO WebClient mocking해서 단위테스트하기.
        // given
        MultiValueMap<String, String> queryParams = null;
        String jsonResponse = "{\"evaluateProfitLossRate\": \"-37.9\"}";

        /*given(webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/trading/inquire-balance")
                        .queryParams(any(MultiValueMap.class)).build()
                )
                .headers(any(Consumer.class))
                .retrieve()
                .bodyToMono(OverseasStockBalanceApiResDto.class)
                .block())
                .willReturn(jsonResponse);*/
        /*webClient.exchangeFunction(clientRequest -> {
            ClientResponse mockResponse = ClientResponse.create(clientRequest.method())
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
            return Mono.just(mockResponse);
        }).build();
        */
        // when
        Mono<OverseasStockBalanceApiResDto> responseMono = webClient.get()
                .uri("/uapi/domestic-stock/v1/trading/inquire-balance")
                .retrieve()
                .bodyToMono(OverseasStockBalanceApiResDto.class);

        // then

        /*StepVerifier.create(responseMono)
                .expectNextMatches(responseDTO -> responseDTO.g().equals(-37.9f))
                .verifyComplete();*/
    }
    @Disabled
    @DisplayName("평가손익률, 보유 수량, 매입 금액, 현재가를 포함한 해외 주식 잔고를 조회한다.")
    @Test
    void getMyStockBalance_baeldung() {
        // given
        String jsonResponse = "{\n" +
                "    \"ctx_area_fk200\": \"\",\n" +
                "    \"ctx_area_nk200\": \"\",\n" +
                "    \"output1\": [\n" +
                "        {\n" +
                "            \"cano\": \"50112033\",\n" +
                "            \"acnt_prdt_cd\": \"01\",\n" +
                "            \"prdt_type_cd\": \"100\",\n" +
                "            \"ovrs_pdno\": \"BAC\",\n" +
                "            \"ovrs_item_name\": \"뱅크오브아메리카\",\n" +
                "            \"frcr_evlu_pfls_amt\": \"0.555000\",\n" +
                "            \"evlu_pfls_rt\": \"0.71\",\n" +
                "            \"pchs_avg_pric\": \"38.9620\",\n" +
                "            \"ovrs_cblc_qty\": \"2\",\n" +
                "            \"ord_psbl_qty\": \"2\",\n" +
                "            \"frcr_pchs_amt1\": \"77.92500\",\n" +
                "            \"ovrs_stck_evlu_amt\": \"78.48000000\",\n" +
                "            \"now_pric2\": \"39.240000\",\n" +
                "            \"tr_crcy_cd\": \"USD\",\n" +
                "            \"ovrs_excg_cd\": \"NYSE\",\n" +
                "            \"loan_type_cd\": \"\",\n" +
                "            \"loan_dt\": \"\",\n" +
                "            \"expd_dt\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"cano\": \"50112033\",\n" +
                "            \"acnt_prdt_cd\": \"01\",\n" +
                "            \"prdt_type_cd\": \"100\",\n" +
                "            \"ovrs_pdno\": \"GME\",\n" +
                "            \"ovrs_item_name\": \"게임스탑\",\n" +
                "            \"frcr_evlu_pfls_amt\": \"-0.701200\",\n" +
                "            \"evlu_pfls_rt\": \"-2.38\",\n" +
                "            \"pchs_avg_pric\": \"29.4010\",\n" +
                "            \"ovrs_cblc_qty\": \"1\",\n" +
                "            \"ord_psbl_qty\": \"1\",\n" +
                "            \"frcr_pchs_amt1\": \"29.40120\",\n" +
                "            \"ovrs_stck_evlu_amt\": \"28.70000000\",\n" +
                "            \"now_pric2\": \"28.700000\",\n" +
                "            \"tr_crcy_cd\": \"USD\",\n" +
                "            \"ovrs_excg_cd\": \"NYSE\",\n" +
                "            \"loan_type_cd\": \"\",\n" +
                "            \"loan_dt\": \"\",\n" +
                "            \"expd_dt\": \"\"\n" +
                "        },\n" +
                "    \"output2\": {\n" +
                "        \"frcr_pchs_amt1\": \"454726.15080\",\n" +
                "        \"ovrs_rlzt_pfls_amt\": \"0.00000\",\n" +
                "        \"ovrs_tot_pfls\": \"1515.22920\",\n" +
                "        \"rlzt_erng_rt\": \"0.00000000\",\n" +
                "        \"tot_evlu_pfls_amt\": \"1515.22920000\",\n" +
                "        \"tot_pftrt\": \"0.33321796\",\n" +
                "        \"frcr_buy_amt_smtl1\": \"454726.150800\",\n" +
                "        \"ovrs_rlzt_pfls_amt2\": \"0.00000\",\n" +
                "        \"frcr_buy_amt_smtl2\": \"454726.150800\"\n" +
                "    },\n" +
                "    \"rt_cd\": \"0\",\n" +
                "    \"msg_cd\": \"20310000\",\n" +
                "    \"msg1\": \"모의투자 조회가 완료되었습니다.\"\n" +
                "}";

        given(openApiProperties.getOauth()).willReturn("oauth");
        given(openApiProperties.getAccntNumber()).willReturn("1111111");
        given(openApiProperties.getAccntProductCode()).willReturn("01");
//        given(openApiRequest.httpGetRequest(any(OpenApiType.class), any(Consumer.class), any(MultiValueMap.class))).willReturn(givenResponse);
        // when
        OverseasStockBalanceApiResDto result = overseasMyService.getMyStockBalance();
        // then
        assertThat(result.getMyStockBalance()).hasSize(2)
                .extracting("evaluateProfitLossRate", "orderPossibleQuantity", "currentPrice")
                .containsExactly(
                        tuple(0.71F, 2, new BigDecimal("39.240000F")),
                        tuple(-0.701200F, 1, new BigDecimal("28.700000F"))
                );
    }
}