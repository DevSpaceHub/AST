/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MessageProducerIntegrationTest
 creation : 2024.9.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.infra.kafka.producer;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.notification.dto.DefaultItemInfoDto;
import com.devspacehub.ast.infra.kafka.dto.MessageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("local")
class MessageProducerIntegrationTest {
    @Autowired
    MessageProducer messageProducer;

    // TODO 발행된 데이터를 확인하는 방법 있을지 확인 필요
    @DisplayName("주문 결과 데이터를 발행한다.")
    @Test
    void produce() {
        MessageDto given = DefaultItemInfoDto.ItemOrderResultDto.ItemOrderResultDto.builder()
                .title("주문 결과 테스트")
                .openApiType(OpenApiType.DOMESTIC_STOCK_BUY_ORDER)
                .itemCode("005930")
                .orderTime("090100")
                .orderPrice(BigDecimal.valueOf(63000))
                .orderQuantity(2)
                .build();

        messageProducer.produce("Stock Result Notification_TEST", given);
    }

}