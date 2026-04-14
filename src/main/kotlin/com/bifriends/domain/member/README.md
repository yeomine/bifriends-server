# Member 도메인

회원 관리 및 Google OAuth2 인증을 담당하는 도메인.

## 패키지 구조

```
domain/member/
├── controller/
│   └── OAuthController.kt          # 인증 API
├── service/
│   ├── MemberService.kt            # 회원 생성/조회 로직
│   └── CustomOAuth2UserService.kt  # Spring Security OAuth2 연동
├── model/
│   ├── Member.kt                   # 회원 엔티티
│   └── Role.kt                     # 역할 Enum (ROLE_USER, ROLE_ADMIN)
└── repository/
    └── MemberRepository.kt         # JPA Repository
```

## API

### `POST /api/v1/members/auth/google`

Flutter 앱에서 Google Sign-In으로 받은 ID 토큰을 전달하면, 서버에서 토큰 검증 후 회원가입 또는 로그인을 처리하고 JWT를 발급한다.

**인증**: 불필요

**Request Body**:

```json
{
  "idToken": "google_id_token_string"
}
```

**Response (200)**:

```json
{
  "accessToken": "jwt_access_token",
  "refreshToken": "jwt_refresh_token",
  "email": "user@gmail.com",
  "name": "구글이름",
  "profileImageUrl": "https://...",
  "onboardingCompleted": false
}
```

- `onboardingCompleted`가 `false`이면 클라이언트는 온보딩 플로우로 이동
- `onboardingCompleted`가 `true`이면 홈 화면으로 이동

## 주요 로직

### 회원 생성/로그인 (`MemberService.findOrCreateMember`)

1. `providerId`(Google 고유 ID)로 기존 회원 조회
2. **기존 회원** → `lastLoginAt` 갱신 후 반환
3. **신규 회원** → `Member` 엔티티 생성 및 DB 저장

### Google ID 토큰 검증 (`GoogleTokenVerifier`)

- Google API Client 라이브러리로 ID 토큰의 서명과 만료를 검증
- `client-id`와 토큰의 `audience`가 일치하는지 확인
- 검증 실패 시 `IllegalArgumentException` 발생

## 엔티티

### Member

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | PK, 자동 생성 |
| `email` | String | Google 이메일 (unique) |
| `name` | String | Google 이름 |
| `profileImageUrl` | String? | Google 프로필 이미지 |
| `providerId` | String | Google 고유 ID (unique) |
| `provider` | String | `"google"` |
| `role` | Role | `ROLE_USER` (기본값) |
| `nickname` | String? | 온보딩에서 입력한 이름 |
| `grade` | Int? | 학년 (3~6) |
| `guardianPhone` | String? | 보호자 전화번호 |
| `notificationEnabled` | Boolean | 알림 권한 허용 여부 |
| `microphoneEnabled` | Boolean | 마이크 권한 허용 여부 |
| `onboardingCompleted` | Boolean | 온보딩 완료 여부 |
| `createdAt` | LocalDateTime | 가입 시간 |
| `lastLoginAt` | LocalDateTime | 마지막 로그인 시간 |

### Role

| 값 | 설명 |
|----|------|
| `ROLE_USER` | 일반 사용자 |
| `ROLE_ADMIN` | 관리자 |
