package com.bifriends.domain.onboarding.dto

import com.bifriends.domain.onboarding.model.Interest
import com.bifriends.domain.onboarding.model.ItemType
import jakarta.validation.constraints.*

data class GuardianRequest(
    @field:NotBlank(message = "전화번호는 필수입니다.")
    @field:Pattern(regexp = "^01[016789]\\d{7,8}$", message = "유효한 전화번호 형식이 아닙니다.")
    val phoneNumber: String
)

data class GuardianResponse(
    val verified: Boolean
)

data class ProfileRequest(
    @field:Size(min = 1, max = 20, message = "이름은 1~20자여야 합니다.")
    val nickname: String? = null,

    @field:Min(value = 3, message = "학년은 3 이상이어야 합니다.")
    @field:Max(value = 6, message = "학년은 6 이하여야 합니다.")
    val grade: Int? = null
)

data class ProfileResponse(
    val nickname: String?,
    val grade: Int?
)

data class InterestsRequest(
    @field:NotEmpty(message = "관심사를 최소 1개 선택해야 합니다.")
    @field:Size(max = 3, message = "관심사는 최대 3개까지 선택할 수 있습니다.")
    val interests: List<Interest>
)

data class InterestsResponse(
    val interests: List<Interest>
)

data class GiftRequest(
    @field:NotNull(message = "아이템을 선택해야 합니다.")
    val itemType: ItemType
)

data class GiftResponse(
    val itemType: ItemType,
    val acquiredAt: String
)

data class PermissionsRequest(
    val notificationEnabled: Boolean,
    val microphoneEnabled: Boolean
)

data class PermissionsResponse(
    val notificationEnabled: Boolean,
    val microphoneEnabled: Boolean
)

data class OnboardingCompleteResponse(
    val completed: Boolean
)
