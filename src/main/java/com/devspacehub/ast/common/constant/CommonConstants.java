/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : CommonConstants
 creation : 2024.1.9
 author : Yoonji Moon
 */

package com.devspacehub.ast.common.constant;

/**
 * 애플리케이션 전반에서 사용하는 공통 상수를 관리하는 클래스.
 */
public class CommonConstants {
    /**
     * 애플리케이션에서 DB 저장/수정 시 남기는 Register Id 값
     */
    public static final String REGISTER_ID = "application";
    /**
     * OpenApi 호출이 성공적일 때 응답하는 ResulrCode 값
     */
    public static final String OPENAPI_SUCCESS_RESULT_CODE = "0";
    /**
     * 주문 시 사용하는 지정가 필드
     */
    public static final String ORDER_DIVISION = "00";
    /**
     * 거래량 순위 조회 API 테스트 환경에서 필요한 Json 형태의 응답 데이터 파일 경로.
     */
    public static final String TRADING_VOLUME_RANKING_DATA_SAMPLE_JSON_PATH = "/www/ast/trading-volume/sampleTradingVolumeGetData.json";
    /**
     * 소수점 자릿수 크기 - 넷째자리
     */
    public static final int DECIMAL_SCALE_FOUR = 4;
    /**
     * 소수점 자릿수 크기 - 둘째자리
     */
    public static final int DECIMAL_SCALE_TWO = 2;

}
