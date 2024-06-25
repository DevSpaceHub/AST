/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : SplitBuyPercents
 creation : 2024.2.18
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.dto;

import com.devspacehub.ast.common.utils.BigDecimalUtil;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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
                    .map(percent -> BigDecimalUtil.percentageToDecimal(new BigDecimal(percent)).floatValue()).collect(Collectors.toList());
        return new SplitBuyPercents(percents);
    }

    /**
     * 분할 매수 퍼센트로 나눈 구매 단가 구하기.
     * 주의. Float 값을 BigDecimal로 바로 변환하면 안된다.
     * @param currentPrice 현재가
     * @param percentsIdx 분할 퍼센트 리스트의 인덱스
     * @return 분할 수량으로 계산된 구매 단가
     */
    public BigDecimal calculateOrderPriceBySplitBuyPercents(BigDecimal currentPrice, int percentsIdx) {
        BigDecimal splitBuyPercent = new BigDecimal(percents.get(percentsIdx).toString());
        return currentPrice.subtract((currentPrice.multiply(splitBuyPercent)));
    }
}
