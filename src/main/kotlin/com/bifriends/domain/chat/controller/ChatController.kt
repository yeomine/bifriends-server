package com.bifriends.domain.chat.controller

import com.bifriends.domain.chat.dto.ChatMessageRequest
import com.bifriends.domain.chat.dto.ChatMessageResponse
import com.bifriends.domain.chat.dto.ChatSessionListResponse
import com.bifriends.domain.chat.dto.ChatSessionMessagesResponse
import com.bifriends.domain.chat.service.ChatService
import com.bifriends.infrastructure.security.JwtProvider
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val chatService: ChatService,
    private val jwtProvider: JwtProvider,
) {

    /** FE 채팅 메시지 → BE가 AI로 중계 (JWT 인증) */
    @PostMapping("/messages")
    fun postMessage(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: ChatMessageRequest,
    ): ResponseEntity<ChatMessageResponse> {
        val memberId = jwtProvider.getMemberId(token.removePrefix("Bearer "))
        return ResponseEntity.ok(chatService.sendMessage(memberId, request))
    }

    /** 내 세션 목록 조회 — 최신 수정순 */
    @GetMapping("/sessions")
    fun getSessions(
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<ChatSessionListResponse> {
        val memberId = jwtProvider.getMemberId(token.removePrefix("Bearer "))
        return ResponseEntity.ok(chatService.getSessions(memberId))
    }

    /** 세션 삭제 (본인 세션만) */
    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSession(
        @RequestHeader("Authorization") token: String,
        @PathVariable sessionId: String,
    ) {
        val memberId = jwtProvider.getMemberId(token.removePrefix("Bearer "))
        chatService.deleteSession(memberId, sessionId)
    }

    /** 세션 내 메시지 목록 조회 (본인 세션만) */
    @GetMapping("/my/sessions/{sessionId}/messages")
    fun getSessionMessages(
        @RequestHeader("Authorization") token: String,
        @PathVariable sessionId: String,
    ): ResponseEntity<ChatSessionMessagesResponse> {
        val memberId = jwtProvider.getMemberId(token.removePrefix("Bearer "))
        return ResponseEntity.ok(chatService.getSessionMessagesForMember(memberId, sessionId))
    }
}
