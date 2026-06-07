package com.bifriends.infrastructure.ai.dto

import com.bifriends.domain.onboarding.model.Interest
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

/** BE → AI 채팅 요청 (AI 팀 스펙: snake_case) */
data class AiChatRequest(
    @JsonProperty("member_id")
    val memberId: Long,
    val nickname: String,
    val grade: Int,
    val interests: List<Interest>,
    @JsonProperty("session_id")
    val sessionId: String,
    val message: String,
)

/**
 * AI → BE 채팅 응답
 *
 * - [reply]        : Leo의 텍스트 응답 (AI 스펙 필드명 "reply")
 * - [cta]          : 앱 내 이동/액션 힌트 (구조 AI 팀 확정 전 JsonNode로 수신)
 * - [todosCreated] : Leo가 이번 응답에서 생성한 할 일 목록
 */
data class AiChatResponse(
    val reply: String? = null,
    val cta: JsonNode? = null,
    @JsonProperty("todos_created")
    val todosCreated: List<AiTodoCreated>? = null,
)

data class AiTodoCreated(
    val title: String,
    @JsonProperty("assigned_date")
    val assignedDate: String,
)
