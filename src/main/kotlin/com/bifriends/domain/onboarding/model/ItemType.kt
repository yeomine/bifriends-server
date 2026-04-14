package com.bifriends.domain.onboarding.model

/**
 * 사용자가 4개의 아이템 중 하나를 선택하면 해당 아이템을 사용자 보유 아이템으로 저장한다.
 * 저장된 아이템은 이후 홈 > 레오 꾸미기 > “나의 보물”에 표시된다.
 */

// 아이템 종류는 총 4가지
enum class ItemType {
    GIFT_1,
    GIFT_2,
    GIFT_3,
    GIFT_4
}
