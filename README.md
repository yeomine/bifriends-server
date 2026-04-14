# BiFriends Backend

Spring Boot 기반 백엔드 API 서버.

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Kotlin 2.1, JDK 21 |
| Framework | Spring Boot 3.4 |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA |
| Auth | Spring Security + OAuth2 (Google) + JWT |
| Build | Gradle (Kotlin DSL) |
| Container | Docker, Docker Compose |

## 실행 방법

```bash
# Docker Compose (DB + App)
docker-compose up -d

# 로컬 개발 (DB만 Docker, 앱은 직접 실행)
docker-compose up -d db
./gradlew bootRun
```

### 환경변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `DB_HOST` | DB 호스트 | `localhost` |
| `DB_PORT` | DB 포트 | `5432` |
| `DB_NAME` | DB 이름 | `bifriends` |
| `DB_USERNAME` | DB 사용자 | `bifriends` |
| `DB_PASSWORD` | DB 비밀번호 | `bifriends` |
| `GOOGLE_CLIENT_ID` | Google OAuth 클라이언트 ID | (필수) |
| `GOOGLE_CLIENT_SECRET` | Google OAuth 클라이언트 시크릿 | (필수) |
| `JWT_SECRET` | JWT 서명 키 (256bit 이상) | 개발용 기본값 |

## 프로젝트 구조

```
src/main/kotlin/com/bifriends/
├── BiFriendsApplication.kt
├── controller/
│   └── HealthController.kt
├── global/
│   └── config/
│       └── SecurityConfig.kt
├── domain/
│   ├── member/                      # 회원/인증 도메인
│   │   ├── controller/
│   │   │   └── OAuthController.kt
│   │   ├── service/
│   │   │   ├── MemberService.kt
│   │   │   └── CustomOAuth2UserService.kt
│   │   ├── model/
│   │   │   ├── Member.kt
│   │   │   └── Role.kt
│   │   └── repository/
│   │       └── MemberRepository.kt
│   └── onboarding/                  # 온보딩 도메인
│       ├── controller/
│       │   └── OnboardingController.kt
│       ├── service/
│       │   └── OnboardingService.kt
│       ├── dto/
│       │   └── OnboardingDtos.kt
│       ├── model/
│       │   ├── Interest.kt
│       │   ├── ItemType.kt
│       │   ├── MemberInterest.kt
│       │   └── MemberItem.kt
│       └── repository/
│           ├── MemberInterestRepository.kt
│           └── MemberItemRepository.kt
└── infrastructure/
    └── security/
        ├── JwtProvider.kt
        ├── PrincipalDetails.kt
        └── GoogleTokenVerifier.kt
```

## API 명세

### 인증 (Member)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| `POST` | `/api/v1/members/auth/google` | Google ID 토큰으로 로그인/가입 → JWT 발급 | X |

### 온보딩 (Onboarding)

| Method | Path | 설명 | 인증 |
|--------|------|------|------|
| `POST` | `/api/v1/onboarding/guardian` | 보호자 전화번호 저장 | O |
| `PATCH` | `/api/v1/onboarding/profile` | 이름/학년 저장 (부분 업데이트) | O |
| `PUT` | `/api/v1/onboarding/interests` | 관심사 저장 (최대 3개) | O |
| `POST` | `/api/v1/onboarding/gift` | 선물 아이템 선택 | O |
| `PATCH` | `/api/v1/onboarding/permissions` | 알림/마이크 권한 상태 저장 | O |
| `POST` | `/api/v1/onboarding/complete` | 온보딩 완료 처리 | O |

### 기능 명세 → API 매핑

| 기능 | API | 백엔드 동작 |
|------|-----|------------|
| 앱 진입 (구글 로그인) | `POST /api/v1/members/auth/google` | ID 토큰 검증 → 회원 생성/로그인 → JWT + `onboardingCompleted` 응답 |
| 보호자 인증 | `POST /api/v1/onboarding/guardian` | 전화번호를 `members.guardian_phone`에 저장 |
| 레오 소개 | 백엔드 API 없음 | 클라이언트 전용 화면 |
| 이름 입력 | `PATCH /api/v1/onboarding/profile` | `nickname` 저장 |
| 인사 화면 | 백엔드 API 없음 | 클라이언트 전용 화면 |
| 학년 선택 | `PATCH /api/v1/onboarding/profile` | `grade` 저장 (같은 엔드포인트, 부분 업데이트) |
| 관심사 선택 | `PUT /api/v1/onboarding/interests` | `member_interests` 테이블에 최대 3개 저장 |
| 선물 선택 | `POST /api/v1/onboarding/gift` | `member_items` 테이블에 아이템 저장 |
| 부모 안내 메시지 | 백엔드 API 없음 | 클라이언트 전용 화면 |
| 권한 요청 | `PATCH /api/v1/onboarding/permissions` | 알림/마이크 권한 상태 저장 |
| 온보딩 완료 | `POST /api/v1/onboarding/complete` | `onboarding_completed = true` 설정 |

## DB 스키마

### members

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 자동 생성 |
| `email` | VARCHAR UNIQUE | Google 이메일 |
| `name` | VARCHAR | Google 이름 |
| `profile_image_url` | VARCHAR NULL | Google 프로필 이미지 |
| `provider_id` | VARCHAR UNIQUE | Google 고유 ID (sub) |
| `provider` | VARCHAR | `"google"` |
| `role` | VARCHAR | `ROLE_USER` / `ROLE_ADMIN` |
| `nickname` | VARCHAR NULL | 온보딩에서 입력한 이름 |
| `grade` | INTEGER NULL | 학년 (3~6) |
| `guardian_phone` | VARCHAR NULL | 보호자 전화번호 |
| `notification_enabled` | BOOLEAN | 알림 권한 |
| `microphone_enabled` | BOOLEAN | 마이크 권한 |
| `onboarding_completed` | BOOLEAN | 온보딩 완료 여부 |
| `created_at` | TIMESTAMP | 가입 시간 |
| `last_login_at` | TIMESTAMP | 마지막 로그인 |

### member_interests

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 자동 생성 |
| `member_id` | BIGINT FK → members | 회원 ID |
| `interest` | VARCHAR | 관심사 Enum |

### member_items

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 자동 생성 |
| `member_id` | BIGINT FK → members | 회원 ID |
| `item_type` | VARCHAR | 아이템 Enum |
| `acquired_at` | TIMESTAMP | 획득 시간 |

## 설계 규칙

### API

- **base path**: `/api/v1`
- **네이밍**: 명사형 복수 (ex: `/api/v1/members`, `/api/v1/posts`)
- **HTTP Method**: REST 표준 준수
  - `GET` 조회 / `POST` 생성 / `PUT` 전체 수정 / `PATCH` 부분 수정 / `DELETE` 삭제
- **응답**: JSON

### DDD 패키지 구조

새 도메인 추가 시 아래 구조를 따른다:

```
domain/{도메인명}/
├── controller/     # @RestController — 요청/응답 처리만 담당
├── service/        # @Service — 비즈니스 로직, 트랜잭션 경계
├── model/          # @Entity, enum, VO — 도메인 모델
└── repository/     # JpaRepository interface
```

- **controller** → service만 의존. 다른 도메인의 controller를 직접 호출하지 않는다.
- **service** → 같은 도메인의 repository 의존. 다른 도메인은 해당 도메인의 service를 통해 접근한다.
- **model** → 순수 도메인 객체. 다른 레이어에 의존하지 않는다.
- **repository** → Spring Data JPA interface만 선언한다.

### global vs infrastructure

| 패키지 | 용도 | 예시 |
|--------|------|------|
| `global/config` | Spring 전역 설정 | SecurityConfig, WebConfig, CorsConfig |
| `global/exception` | 공통 예외 처리 | GlobalExceptionHandler, ErrorResponse |
| `global/common` | 공통 DTO, 유틸 | BaseEntity, PageResponse |
| `infrastructure/` | 외부 기술 구현체 | JWT, S3, Redis, 외부 API 클라이언트 |

### 컨벤션

- Entity 클래스에 `data class` 사용 금지 — JPA 프록시 호환 이슈
- Repository는 `JpaRepository<Entity, Long>` 상속
- REST Controller에는 `@RequestMapping("/api/v1/{도메인}")` prefix 사용
- 환경 설정값은 `application.yml`에 환경변수(`${VAR}`)로 관리 — 하드코딩 금지
