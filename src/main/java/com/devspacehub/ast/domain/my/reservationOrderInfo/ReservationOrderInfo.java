/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ReservationOrderInfo
 creation : 2024.3.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.my.reservationOrderInfo;

import com.devspacehub.ast.common.constant.CommonConstants;
import com.devspacehub.ast.domain.orderTrading.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 예약 매수 Entity.
 */
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation_order_info")
@Entity
public class ReservationOrderInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "item_code", length = 6)
    private String itemCode;

    @Column(name = "korean_item_name")
    private String koreanItemName;
    @Column(name = "order_price")
    private BigDecimal orderPrice;

    @Column(name = "order_quantity")
    private int orderQuantity;
    @Column(name = "order_start_date")
    private LocalDate orderStartDate;
    @Column(name = "order_end_date")
    private LocalDate orderEndDate;

    @Column(nullable = false)
    private int priority;
    @Column(name = "use_yn")
    private char useYn; // TODO YesNoStatus enum으로 변경
    @Setter
    @Column(name = "conclusion_quantity", nullable = false)
    private int conclusionQuantity;

    @Setter
    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;
    @Column(name = "update_id")
    private String updateId;

    /**
     * 주문 금액을 조정한다.
     * @param orderPrice
     */
    public void updateOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    /**
     * 예약 설정해놓은 orderQuantity의 수량만큼 모두 체결되어 예약 매수 사용 여부를 비활성화한다.
     */
    public void disable() {
        this.useYn = 'N';
    }

    /**
     * 주문 수량만큼 모두 체결됐는지 체크한다.
     * @param concludedQuantity
     * @return
     */
    public boolean checkTotalConcluded(int concludedQuantity) {
        return this.orderQuantity == concludedQuantity;
    }

    /**
     * 금일 체결된 주문 수량을 더한다.
     * @param concludedQuantity
     */
    public void addConcludedQuantity(int concludedQuantity) {
        this.conclusionQuantity += concludedQuantity;
    }

    /**
     * 주문할 수량에서 이미 체결된 수량만큼 감소시킨다.
     */
    public void subtractConcludedQuantity(int concludedQuantity) {
        this.orderQuantity -= concludedQuantity;
    }

    /**
     * 해당 엔티티 업데이트 시 함께 업데이트한다.
     * @param now
     */
    public void updateMetaData(LocalDateTime now) {
        this.updateDatetime = now;
        this.updateId = CommonConstants.REGISTER_ID;
    }
}
