package com.bifriends.domain.onboarding.model
"""사용자는 관심사를 최대 3개까지 선택할 수 있다. 
선택 완료 후 다음 버튼을 누르면 선택된 관심사를 사용자 데이터에 저장한다.
관심사는 홈 > 레오 꾸미기 > “나의 관심사”에서 확인할 수 있다.

"""
#관심사 9종
enum class Interest {
    DINOSAUR,
    ANIMAL,
    SPACE,
    SPORTS,
    KPOP_MUSIC,
    GAME,
    COOKING,
    CRAFTING,
    SCIENCE
}
