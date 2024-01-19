# AST 프로젝트
### 자동 (AST : Awake (항상 깨어있는), Auto (자동화된)) 주식 (Stock) 매매 (Trade)

---
### 전체 구성도
<img src="https://github.com/DevSpaceHub/AST/assets/66311276/64b326e7-3063-4ec7-98fd-86453ea70d61" width="310" height="315"/>

### 중심 기능
- 매수/매도 알고리즘 로직에 따른 주문 처리
- KIS Developer OpenApi 활용 : RESTful API, Web Socket
- ~배치 서비스인 AST_BATCH에서 매일 8:55AM 에 토큰 발급 요청 시, 발급된 토큰 캐싱 처리~

### 중심 기술
- Cloud : GCP (인스턴스 OS : Debian GNU/Linux 11)
- DB : MySQL 8.0
- Application : Spring Boot 3.2, JPA, WebClient, Java 17, Gradle
