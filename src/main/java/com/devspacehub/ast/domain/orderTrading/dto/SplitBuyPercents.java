/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SplitBuy
 creation : 2024.2.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 분할 매수 전략 위한 퍼센트 관리 일급 컬렉션.
 */
@Getter
public class SplitBuyPercents {
    List<Float> percents;

    public SplitBuyPercents() {
        percents = new ArrayList<>(Arrays.asList(0.2F, 0.5F, 0.8F));
    }

    public Float getCalculatedSplitBuyPrice(int idx, int currentPrice) {
        return currentPrice - (currentPrice * percents.get(idx));
    }
}
