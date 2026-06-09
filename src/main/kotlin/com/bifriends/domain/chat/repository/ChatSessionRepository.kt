package com.bifriends.domain.chat.repository

import com.bifriends.domain.chat.model.ChatSession
import org.springframework.data.jpa.repository.JpaRepository

interface ChatSessionRepository : JpaRepository<ChatSession, Long> {
    /** FE UUID(sessionKey)로 세션 조회 */
    fun findBySessionKey(sessionKey: String): ChatSession?
    /** 회원의 세션 목록 — 최신 수정순 */
    fun findAllByMemberIdOrderByUpdatedAtDesc(memberId: Long): List<ChatSession>
    fun deleteAllByMemberId(memberId: Long)
}
