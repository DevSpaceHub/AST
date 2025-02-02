/*
 © 2025 devspacehub, Inc. All rights reserved.

 name : NotificatorTest
 creation : 2025.1.26
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.notification;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.notification.dto.MessageContentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class NotificatorTest {
    @InjectMocks
    private Notificator notificator;

    @Test
    @DisplayName("알림 발송 시 에러 발생해도 로그만 작성하여 exception을 던지지 않는다.")
    void doNotThrowExceptionAboutNotificationError() {
        ReflectionTestUtils.setField(notificator, "discordWebhookUrl", "https://discord.com/api/webhooks/12345/wrongUrl");
        MessageContentDto.ConclusionResult given = MessageContentDto.ConclusionResult.builder()
                .title("체결 완료")
                .accountStatusKor("모의")
                .itemNameKor("삼성전자")
                .itemCode("005930")
                .openApiType(OpenApiType.DOMESTIC_ORDER_CONCLUSION_FIND)
                .orderQuantity(10)
                .orderPrice(BigDecimal.valueOf(80000))
                .orderNumber("0123456")
                .orderTime("090130")
                .concludedQuantity(10)
                .concludedPrice(BigDecimal.valueOf(80000))
                .build();

        assertDoesNotThrow(() -> notificator.sendMessage(given));
    }
}
