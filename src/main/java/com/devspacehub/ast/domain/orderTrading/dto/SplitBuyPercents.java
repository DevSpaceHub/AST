/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SplitBuyPercents
 creation : 2024.2.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 분할 매수 전략 위한 퍼센트 관리 일급 컬렉션.
 */
@Getter
public class SplitBuyPercents {
    private final List<Float> percents;

    public SplitBuyPercents(Float... percent) {
        if (Objects.isNull(percent)) {
            // TODO 분할 매수/전량 매수 모두 고려하는 상황 : 추후 필요 시 검토
        }
        percents = Arrays.asList(percent);
    }

    /**
     * 분할 매수 퍼센트로 나눈 구매 단가 구하기.
     * @param currentPrice
     * @param percentsIdx
     * @return
     */
    public Float calculateBuyPriceBySplitBuyPercents(int currentPrice, int percentsIdx) {
        return currentPrice - (currentPrice * percents.get(percentsIdx));
    }
}
