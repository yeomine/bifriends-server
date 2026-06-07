package com.bifriends.infrastructure.ai

import com.bifriends.infrastructure.ai.dto.AiChatRequest
import com.bifriends.infrastructure.ai.dto.AiChatResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class AiChatClient(
    private val restClient: RestClient,
    private val properties: AiServiceProperties,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(AiChatClient::class.java)

    fun sendChat(request: AiChatRequest): AiChatResponse {
        if (!properties.enabled) {
            return AiChatResponse(reply = null)
        }
        return runCatching {
            restClient.post()
                .uri(properties.chatPath)
                .postJson(request)
                .exchange { _, response ->
                    if (!response.statusCode.is2xxSuccessful) {
                        val body = response.body.readAllBytes().decodeToString()
                        log.error("AI 서버 오류 [{}]: {}", response.statusCode, body)
                        return@exchange AiChatResponse(reply = null)
                    }
                    runCatching {
                        objectMapper.readValue(response.body.readAllBytes(), AiChatResponse::class.java)
                    }.getOrElse { e ->
                        log.error("AI 응답 파싱 실패: {}", e.message)
                        AiChatResponse(reply = null)
                    }
                } ?: AiChatResponse(reply = null)
        }.getOrElse { e ->
            log.error("AI 서버 연결 실패: {}", e.message)
            AiChatResponse(reply = null)
        }
    }
}
