/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : StockItemDtoTest
 creation : 2024.4.29
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.marketStatus.dto;

import com.devspacehub.ast.common.constant.ExchangeCode;
import com.devspacehub.ast.domain.my.stockBalance.dto.response.overseas.OverseasStockBalanceApiResDto;
import com.devspacehub.ast.exception.error.DtoConversionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * StockItemDto 테스트
*/
class StockItemDtoTest {
    @DisplayName("주식잔고조회 응답 DTO의 값으로 StockItemDto 인스턴스를 생성한다.")
    @Test
    void of_overseas() {
        BigDecimal givenOrderPrice = new BigDecimal("142.103900");
        OverseasStockBalanceApiResDto.MyStockBalance given = new OverseasStockBalanceApiResDto.MyStockBalance(
                "", "", "", "AAPL", "애플", "", null, "", "", 10, "", "", givenOrderPrice, "", "NASD", "", "", ""
        );

        StockItemDto result = StockItemDto.Overseas.of(given);

        assertThat(result.getOrderPrice()).isEqualTo("142.1039");
        assertThat(result.getItemNameKor()).isEqualTo("애플");
        assertThat(result.getItemCode()).isEqualTo("AAPL");
    }

    @DisplayName("인스턴스가 해외 클래스 타입이면 Overseas 클래스로 캐스팅한다.")
    @Test
    void castToOverseasTest() {
        StockItemDto given = StockItemDto.Overseas.builder().exchangeCode(ExchangeCode.NASDAQ).build();

        StockItemDto.Overseas result = given.castToOverseas();

        assertThat(result.getExchangeCode()).isEqualTo(ExchangeCode.NASDAQ);
    }

    @DisplayName("인스턴스가 국내 클래스 타입이면 DtoConversionException을 발생시킨다.")
    @Test
    void throw_DtoConversionException_when_given_is_Domestic() {
        StockItemDto given = StockItemDto.Domestic.builder().itemCode("삼성").build();

        assertThatThrownBy(given::castToOverseas)
                .isInstanceOf(DtoConversionException.class);
    }
}