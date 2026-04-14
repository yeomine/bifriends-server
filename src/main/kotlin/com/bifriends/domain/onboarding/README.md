# Onboarding 도메인

사용자 온보딩 플로우(보호자 인증 → 프로필 입력 → 관심사/선물 선택 → 권한 요청 → 완료)를 처리하는 도메인.

## 패키지 구조

```
domain/onboarding/
├── controller/
│   └── OnboardingController.kt      # 온보딩 API (6개 엔드포인트)
├── service/
│   └── OnboardingService.kt         # 온보딩 비즈니스 로직
├── dto/
│   └── OnboardingDtos.kt            # 요청/응답 DTO 전체
├── model/
│   ├── Interest.kt                  # 관심사 Enum (9종)
│   ├── ItemType.kt                  # 선물 아이템 Enum (4종)
│   ├── MemberInterest.kt           # 회원-관심사 엔티티
│   └── MemberItem.kt               # 회원-아이템 엔티티
└── repository/
    ├── MemberInterestRepository.kt
    └── MemberItemRepository.kt
```

## 온보딩 플로우

```
구글 로그인 → 보호자 인증 → 레오 소개(FE) → 이름 입력 → 인사 화면(FE)
→ 학년 선택 → 관심사 선택 → 선물 선택 → 부모 안내(FE) → 권한 요청 → 완료
```

- **(FE)** 표시: 클라이언트 전용 화면으로 백엔드 API 호출 없음
- 온보딩 중간 진행 상태는 저장하지 않음 (중단 시 처음부터 다시 시작)

## API

모든 API는 JWT 인증 필요 (`Authorization: Bearer {token}`)

### `POST /api/v1/onboarding/guardian`

보호자 전화번호를 저장한다.

**Request**:
```json
{ "phoneNumber": "01012345678" }
```

**Response (200)**:
```json
{ "verified": true }
```

**Validation**: `01X`로 시작하는 10~11자리 숫자

---

### `PATCH /api/v1/onboarding/profile`

이름 또는 학년을 부분 업데이트한다. 이름 입력 단계와 학년 선택 단계에서 각각 호출된다.

**Request (이름 입력)**:
```json
{ "nickname": "지민" }
```

**Request (학년 선택)**:
```json
{ "grade": 4 }
```

**Response (200)**:
```json
{ "nickname": "지민", "grade": 4 }
```

**Validation**: 이름 1~20자, 학년 3~6

---

### `PUT /api/v1/onboarding/interests`

관심사를 최대 3개 선택하여 저장한다. 기존 관심사를 모두 삭제하고 새로 저장한다.

**Request**:
```json
{ "interests": ["DINOSAUR", "SPACE", "GAME"] }
```

**Response (200)**:
```json
{ "interests": ["DINOSAUR", "SPACE", "GAME"] }
```

**Validation**: 1~3개 필수, 중복 자동 제거

**관심사 목록**:

| Enum | 한글 |
|------|------|
| `DINOSAUR` | 공룡 |
| `ANIMAL` | 동물 |
| `SPACE` | 우주 |
| `SPORTS` | 스포츠 |
| `KPOP_MUSIC` | K-POP/음악 |
| `GAME` | 게임 |
| `COOKING` | 요리/맛집 |
| `CRAFTING` | 만들기/그리기 |
| `SCIENCE` | 과학/실험 |

---

### `POST /api/v1/onboarding/gift`

선물 아이템을 선택하여 사용자 보유 아이템으로 저장한다. 저장된 아이템은 홈 > 레오 꾸미기 > "나의 보물"에서 조회된다.

**Request**:
```json
{ "itemType": "GIFT_1" }
```

**Response (200)**:
```json
{ "itemType": "GIFT_1", "acquiredAt": "2026-04-14T12:00:00" }
```

**아이템 종류**: `GIFT_1`, `GIFT_2`, `GIFT_3`, `GIFT_4`

---

### `PATCH /api/v1/onboarding/permissions`

알림 및 마이크 권한 허용 상태를 저장한다.

**Request**:
```json
{ "notificationEnabled": true, "microphoneEnabled": true }
```

**Response (200)**:
```json
{ "notificationEnabled": true, "microphoneEnabled": true }
```

---

### `POST /api/v1/onboarding/complete`

온보딩을 완료 처리한다. 이후 로그인 시 `onboardingCompleted: true`가 응답되어 홈 화면으로 바로 이동한다.

**Request**: 없음 (Authorization 헤더만 필요)

**Response (200)**:
```json
{ "completed": true }
```

## 엔티티

### MemberInterest

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | PK |
| `member` | Member (FK) | 회원 |
| `interest` | Interest (Enum) | 관심사 |

### MemberItem

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | PK |
| `member` | Member (FK) | 회원 |
| `itemType` | ItemType (Enum) | 아이템 종류 |
| `acquiredAt` | LocalDateTime | 획득 시간 |
