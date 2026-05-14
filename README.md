# 🚗 중고차 거래 플랫폼

> Spring Boot 기반 중고차 판매 및 관리 웹 애플리케이션

---

## 📌 프로젝트 소개

중고차 등록, 조회, 판매를 지원하는 웹 플랫폼입니다.
사용자는 차량을 등록하고, 구매자는 다양한 조건으로 차량을 검색할 수 있습니다.

프론트엔드와 백엔드를 직접 구현하여 **실제 서비스 흐름**을 고려한 구조로 개발했습니다.

---

## 🛠 기술 스택

### 🔹 Backend

* Java 17
* Spring Boot
* Spring Security (인증/인가)
* JPA (Hibernate)

### 🔹 Frontend

* HTML / CSS / JavaScript
* Ajax (비동기 처리)

### 🔹 Database

* MariaDB

### 🔹 Build Tool

* Gradle

---

## ⚙️ 주요 기능

### 🔐 회원 기능

* 회원가입 / 로그인
* Spring Security 기반 인증 처리
* 마이페이지

---

### 🚗 차량 등록 및 관리

* 차량 등록 (이미지 포함)
* 임시 저장 → 실제 등록 기능
* 차량 정보 수정 / 삭제

---

### 🔍 차량 검색

* 조건별 검색 기능
* Ajax 기반 동적 결과 업데이트
* 빠른 UX 제공

---

### 🖼 이미지 관리

* 차량 이미지 별도 테이블 관리 (`car_image`)
* Draft 데이터 → 실제 차량 등록 시 이미지 자동 복사

---

### 📄 마이페이지

* 판매 차량 관리
* 구매 문의 내역
* 사용자 정보 관리

---

## 🧩 시스템 구조

```
[Frontend]
  ↓ (Ajax 요청)
[Spring Boot Controller]
  ↓
[Service Layer]
  ↓
[JPA Repository]
  ↓
[MariaDB]
```

---

## 👨‍💻 담당 역할

* 프론트엔드 페이지 구현 (HTML / CSS / JS)
* Ajax 기반 UI 동적 처리
* 백엔드 데이터 처리 및 DB 연동
* 차량 등록/조회/관리 기능 구현
* 마이페이지 기능 구현

---

## 🚀 실행 방법

```bash
# 1. 프로젝트 클론
git clone https://github.com/your-repo.git

# 2. DB 설정 (MariaDB)
application.yml 또는 application.properties 수정

# 3. 실행
./gradlew bootRun
```

---

## 📸 실행 화면(메인 화면)

<img width="844" height="1023" alt="image" src="https://github.com/user-attachments/assets/9678703d-5fea-4e64-bf1c-1e6048c33026" />

---

## 📝 한 줄 정리

👉 **중고차 등록부터 검색, 관리까지 전체 흐름을 구현한 풀스택 웹 프로젝트**
