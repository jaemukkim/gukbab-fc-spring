# 국밥FC 팀 카페

친구들과 사용하는 국밥FC 웹 카페입니다. 현재는 기능을 하나씩 학습하며 붙일 수 있도록 최소 실행 골격만 구성되어 있습니다.

## 기술 구성

- Java 21
- Spring Boot 3.5
- Maven
- Thymeleaf

데이터베이스, JPA, Spring Security는 해당 기능을 구현하는 단계에서 추가합니다.

## 실행 방법

JDK 21과 Maven을 설치한 후 프로젝트 루트에서 실행합니다.

```powershell
mvn spring-boot:run
```

브라우저에서 `http://localhost:8080`으로 접속합니다.

## 기능 구현 순서

1. 회원가입
2. 로그인과 권한
3. 공지사항
4. 자유게시판
5. 선수 프로필
6. 풋살 일정과 참가 여부
7. 경기 결과
8. 유니폼 구매 신청

기능별 패키지는 `controller`, `service`, `repository`, `domain`의 역할을 구분하되, 작은 프로젝트에 맞게 과도하게 나누지 않습니다.
