/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : OrderConclusionFindExternalReqDtoTest
 creation : 2024.9.5
 author : Yoonji Moon
 */
package com.devspacehub.ast.domain.my.dto.orderConclusion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OrderConclusionFindExternalReqDtoTest {

    @DisplayName("해외 주식은 장 시작 일자가 종료 일자보다 하루 전으로 세팅되어야 한다.")
    @Test
    void startDate_is_one_day_earlier_than_endDate_only_Overseas() {
        LocalDate given = LocalDate.of(2024, 9, 5);

        MultiValueMap<String, String> result = OrderConclusionFindExternalReqDto.Overseas.createParameter("12345678", "01", given);

        assertThat(result.get("ORD_STRT_DT").get(0)).isEqualTo("20240904");
        assertThat(result.get("ORD_END_DT").get(0)).isEqualTo("20240905");
    }

}