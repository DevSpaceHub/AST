/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : NotificatorTest
 creation : 2024.9.22
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.notification;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.notification.dto.ItemConclusionResultDto;
import com.devspacehub.ast.infra.kafka.producer.MessageProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificatorTest {
    @InjectMocks
    Notificator notificator;
    @Mock
    MessageProducer messageProducer;

    @DisplayName("토픽명과 메세지 데이터를 전달하여 메세지 발송을 요청한다.")
    @Test
    void sendStockResultMessage() {
        System.setProperty("spring.profiles.active", "test");
        ItemConclusionResultDto given = ItemConclusionResultDto.builder()
                .openApiType(OpenApiType.OVERSEAS_ORDER_CONCLUSION_FIND)
                .itemCode("AAPL")
                .orderTime("105010")
                .orderPrice(BigDecimal.valueOf(228.20))
                .orderQuantity(2)
                .concludedPrice(BigDecimal.valueOf(228.20))
                .concludedQuantity(2)
                .build();

        notificator.sendStockResultMessage(given);

        verify(messageProducer, only()).produce("Stock-Result-Notification_TEST", given);
    }
}