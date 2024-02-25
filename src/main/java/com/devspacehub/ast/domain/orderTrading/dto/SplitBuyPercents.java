/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SplitBuyPercents
 creation : 2024.2.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import com.devspacehub.ast.util.NumberUtil;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 분할 매수 전략 위한 퍼센트 관리 일급 컬렉션.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SplitBuyPercents {
    private static final String COMMA_FOR_SPLIT = ",";
    private final List<Float> percents;

    /**
     * 인자로 들어오는 String 타입 퍼센츠 값을 일급 컬렉션 SplitBuyPercents로 변환 & 응답한다.
     * @param splitBuyPercentsByComma
     * @return
     */
    public static SplitBuyPercents of(String splitBuyPercentsByComma) {
        if (StringUtils.isBlank(splitBuyPercentsByComma)) {
            // TODO 분할 매수/전량 매수 모두 고려하는 상황 : 추후 필요 시 검토
        }
        List<Float> percents = Arrays.stream(splitBuyPercentsByComma.split(COMMA_FOR_SPLIT))
                    .map(percent -> NumberUtil.percentageToDecimal(Integer.parseInt(percent))).collect(Collectors.toList());
        return new SplitBuyPercents(percents);
    }

    /**
     * 분할 매수 퍼센트로 나눈 구매 단가 구하기.
     * @param currentPrice
     * @param percentsIdx
     * @return
     */
    public int calculateOrderPriceBySplitBuyPercents(int currentPrice, int percentsIdx) {
        Float result = currentPrice - (currentPrice * percents.get(percentsIdx));
        return result.intValue();
    }
}
