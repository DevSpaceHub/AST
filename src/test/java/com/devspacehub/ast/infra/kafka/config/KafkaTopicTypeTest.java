/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : KafkaTopicTypeTest
 creation : 2024.9.25
 author : Yoonji Moon
 */

package com.devspacehub.ast.infra.kafka.config;

import com.devspacehub.ast.common.constant.ProfileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTopicTypeTest {

    @DisplayName("환경 ProfileType에 따라 적절한 topic 이름을 반환한다.")
    @Test
    void return_topicName_by_profileType() {
        String result = KafkaTopicType.STOCK_RESULT_NOTI_TOPIC.getValue(ProfileType.TEST);

        assertThat(result).isEqualTo("Stock-Result-Notification_TEST");
    }
}