# AST 프로젝트
### 자동 (AST : Awake (항상 깨어있는), Auto (자동화된)) 주식 (Stock) 매매 (Trade)

---
### 전체 구성도
<img src="https://github.com/DevSpaceHub/AST/assets/66311276/64b326e7-3063-4ec7-98fd-86453ea70d61" width="310" height="315"/>

### 중심 기능
- 매수/매도 알고리즘 로직에 따른 주문 처리
- KIS Developer OpenApi 활용 : RESTful API, Web Socket
- ~배치 서비스인 AST_BATCH에서 매일 8:55AM 에 토큰 발급 요청 시, 발급된 토큰 캐싱 처리~
- 중심 기술
  - 국내 시장 및 해외(나스닥, 뉴욕) 시장에 한하여 서비스. (해외는 예정)
      - 국내 시장 및 해외 시장은 장 시간이 다르다.
  - 특정 지표에 따라 매수/매도 거래 진행
  - 예약 매수 기능 제공
  - 금일 주문 및 체결된 종목에 대해 디스코드 메시지 발송
  - 주식 매매 관련 작업/정보는 KIS OpenApi를 이용

### 중심 기술
- Cloud : GCP (인스턴스 OS : Debian GNU/Linux 11)
- DB : MySQL 8.0
- Application : Spring Boot 3.2, JPA, Querydsl, WebClient, Java 17, Gradle

### ERD
<img src="https://github.com/DevSpaceHub/portfolio/blob/main/AST%20DB%20ERD.png" width="2000" height="600"/>

### 버전 관리
| version (relaese date) | added features |
|---|---|
| 0.0.2　　(24.02.05) | 국내 매수/매도 기능, 디스코드 메시지 발송 기능 도입 |
| 0.0.3　　(24.02.14) | 로그 관리, 버그 수정 |
| 0.0.4　　(24.02.25) | 분할 매수 기능 도입 |
| 0.0.5　　(24.04.03) | 예약 매수 기능 일부 도입 |
| 0.1.0　　(24.04.07) | 예약 매수 기능 관련 버그 수정하여 최종 도입 |
| 0.2.0　　(24.05.10) | 체결 결과 기반 후처리 기능(체결 결과 메세지 발송, 예약 매수의 체결 수량에 따라 예약 매수 종목 사용 여부 업데이트 기능) 도입,<br>QueryDsl 추가, 로그 관리, 버그 수정, KIS 호출 지연 시간 수정 |
| 0.2.1　　(24.05.20) | 체결 결과 기반 후처리 기능 관련 버그 수정, 디스코드 메시지 발송 기능 수정(시간 지연) |
| 0.2.2　　(24.06.11) | 애플리케이션 내 전반적인 가격 데이터의 타입을 int -> BigDecimal로 수정 |
| 0.3.0　　(24.06.26) | 해외 매수/매도 기능, 디스코드 메시지 발송 기능 도입 |
| 0.3.1　　(24.08.01) | 해외 예약 매수 주문 기능 도입 |
| 0.3.4　　(24.09.??) | 해외 체결 결과 기반 후처리 기능(예약 매수 종목의 체결 수량에 따라 예약 매수 사용 여부 업데이트, 체결 결과 알림 발송) 도입 |

<br>

## License
```
 Copyright 2023-present DevSpaceHub.

 Licensed under the GPL-3.0 license.
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 https://www.gnu.org/licenses/gpl-3.0.html
```

