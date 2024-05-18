/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : CurrentStockPriceInfoBuilder
 creation : 2024.2.19
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.orderTrading.service;

import com.devspacehub.ast.common.constant.YesNoStatus;
import com.devspacehub.ast.domain.marketStatus.dto.CurrentStockPriceExternalResDto;

/**
 * 테스트 코드 실행을 위한 CurrentStockPriceInfo 빌더 클래스.
 */
public class CurrentStockPriceInfoBuilder {
    public static CurrentStockPriceExternalResDto.CurrentStockPriceInfo buildWith(String per, String pbr, String htsMarketCapitalization,
                                                                                  String accumulationVolume, YesNoStatus invtCarefulYn,
                                                                                  YesNoStatus shortOverYn,  YesNoStatus delistingYn) {
        return CurrentStockPriceExternalResDto.CurrentStockPriceInfo.builder()
                .per(per)
                .pbr(pbr)
                .htsMarketCapitalization(htsMarketCapitalization) // 2000억
                .accumulationVolume(accumulationVolume)
                .invtCarefulYn(invtCarefulYn.getCode())
                .shortOverYn(shortOverYn.getCode())
                .delistingYn(delistingYn.getCode())
                .build();
    }

    public static CurrentStockPriceExternalResDto.CurrentStockPriceInfo buildWithLowerLimitPrice(String lowerLimitPrice) {
        return CurrentStockPriceExternalResDto.CurrentStockPriceInfo.builder()
                .stockLowerLimitPrice(lowerLimitPrice)
                .build();
    }
}
