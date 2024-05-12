/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : MessageContentDto
 creation : 2024.4.24
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.notification.dto;

import com.devspacehub.ast.common.constant.OpenApiType;
import com.devspacehub.ast.domain.orderTrading.OrderTrading;
import com.devspacehub.ast.domain.orderTrading.dto.OrderConclusionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 디스코드 메시지 내용 DTO 추상 클래스.
 */
@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public abstract class MessageContentDto {
    private String title;
    private String accountStatusKor;
    private String itemNameKor;
    private String itemCode;
    private OpenApiType openApiType;
    private int orderQuantity;
    private int orderPrice;
    private String orderNumber;
    private String orderTime;


    /**
     * 알림 메시지 공통 내용 작성
     * @param content MessageContentDto 상속받는 메시지 내용 DTO
     * @return 완성된 메시지 내용
     */
    public <T extends MessageContentDto> String createMessage(T content) {
        StringBuilder sb = new StringBuilder();

        sb.append("**[").append(content.getOrderTime()).append("] ").append(content.getTitle()).append("**");
        sb.append("\n계좌 상태 : ").append(content.getAccountStatusKor());
        sb.append("\n종목명 : ").append(content.getItemNameKor());
        sb.append(" (").append(content.getItemCode()).append(")");
        sb.append("\n매매구분 : ").append(content.getOpenApiType().getDiscription());
        sb.append("\n주문번호 : ").append(content.getOrderNumber());
        sb.append("\n주문수량 : ").append(content.getOrderQuantity()).append("주");
        sb.append("\n주문단가 : ").append(content.getOrderPrice());

        return sb.toString();       
    }

    /**
     * 매수/매도 주문 결과 메세지 DTO
     * MessageContentDto 상속
     */
    @Getter
    @SuperBuilder
    @AllArgsConstructor
    public static class OrderResult extends MessageContentDto{

        public static OrderResult fromOne(OpenApiType openApiType, String accountStatus, OrderTrading orderTrading) {
            return OrderResult.builder()
                    .title("주문 완료")
                    .accountStatusKor(accountStatus)
                    .itemNameKor(orderTrading.getItemNameKor())
                    .itemCode(orderTrading.getItemCode())
                    .openApiType(openApiType)
                    .orderQuantity(orderTrading.getOrderQuantity())
                    .orderPrice(orderTrading.getOrderPrice())
                    .orderNumber(orderTrading.getOrderNumber())
                    .orderTime(orderTrading.getOrderTime())
                    .build();
        }
    }

    /**
     * 체결 결과 메세지 DTO
     * MessageContentDto 상속
     */
    @Getter
    @SuperBuilder
    public static class ConclusionResult extends MessageContentDto {
        private int concludedQuantity;
        private int concludedPrice;

        public static ConclusionResult fromOne(OpenApiType orderApiType, String accountStatus, OrderConclusionDto orderConclusion) {
            return ConclusionResult.builder()
                    .title("체결 완료")
                    .accountStatusKor(accountStatus)
                    .itemNameKor(orderConclusion.getItemNameKor())
                    .itemCode(orderConclusion.getItemCode())
                    .openApiType(orderApiType)
                    .orderQuantity(orderConclusion.getOrderQuantity())
                    .orderPrice(orderConclusion.getOrderPrice())
                    .orderNumber(orderConclusion.getOrderNumber())
                    .concludedQuantity(orderConclusion.getConcludedQuantity())
                    .concludedPrice(orderConclusion.getConcludedPrice())
                    .orderTime(orderConclusion.getOrderTime())
                    .build();
        }

        @Override
        public <T extends MessageContentDto> String createMessage(T content) {
            ConclusionResult embodiedContent = (ConclusionResult) content;

            StringBuilder commonMessage = new StringBuilder(super.createMessage(content));
            commonMessage.append("\n체결수량 : ").append(embodiedContent.getConcludedQuantity()).append("주");
            commonMessage.append("\n체결금액 : ").append(embodiedContent.getConcludedPrice());
            return commonMessage.toString();
        }
    }
}
