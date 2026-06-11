package com.bifriends.infrastructure.ai

import com.bifriends.infrastructure.ai.dto.AiEmotionScenarioRequest
import com.bifriends.infrastructure.ai.dto.AiEmotionScenarioResponse
import com.bifriends.infrastructure.ai.dto.AiScenarioJobAccepted
import com.bifriends.infrastructure.ai.dto.AiScenarioStatusResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
/**
 * BE → AI 감정 학습 시나리오 생성 클라이언트 (EMO-04)
 *
 * 친구랑 탭에서 "이야기 보러 가기!" 버튼 클릭 시 호출된다.
 * AI는 4단계(step1~4) 감정 학습 세트를 생성하여 반환한다.
 *
 * step3 만화 이미지:
 * - 정상: AI가 image_b64로 반환 → BE가 Firebase Storage 업로드 후 image_url로 교체
 * - 폴백: AI가 image_url로 직접 반환 (is_fallback = true)
 */
@Component
class AiEmotionScenarioClient(
    private val restClient: RestClient,
    private val properties: AiServiceProperties,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * AI에 감정 학습 세트 생성을 요청한다.
     *
     * @return AI 응답 (step3 image_b64 포함 가능성 있음)
     * @throws IllegalStateException AI 응답이 비어 있거나 호출 실패 시
     */
    fun generateScenario(request: AiEmotionScenarioRequest): AiEmotionScenarioResponse {
        if (!properties.enabled) {
            log.info(
                "[AiEmotionScenarioClient] AI 연동 비활성화 — 폴백 시나리오 반환 (memberId={})",
                request.memberId
            )
            return buildFallbackScenario(request)
        }

        log.info(
            "[AiEmotionScenarioClient] 시나리오 생성 요청 (memberId={}, emotion={})",
            request.memberId, request.emotion
        )

        log.info("[AiEmotionScenarioClient] 요청 JSON: {}", objectMapper.writeValueAsString(request))

        val responseNode = restClient.post()
            .uri(properties.emotionScenarioPath)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange { _, response ->
                objectMapper.readTree(response.body.readAllBytes())
            } ?: throw IllegalStateException("AI 시나리오 job 응답이 비어 있습니다.")

        // 폴백 시 AI가 시나리오를 즉시 반환 (job_id 없음)
        if (!responseNode.has("job_id")) {
            log.info("[AiEmotionScenarioClient] 즉시 응답 수신 (폴백) — memberId={}", request.memberId)
            return objectMapper.treeToValue(responseNode, AiEmotionScenarioResponse::class.java)
        }

        val job = objectMapper.treeToValue(responseNode, AiScenarioJobAccepted::class.java)
        log.info("[AiEmotionScenarioClient] job 접수 (jobId={}, memberId={})", job.jobId, request.memberId)

        return pollUntilDone(job.jobId, request.memberId)
    }

    private fun pollUntilDone(jobId: String, memberId: Long): AiEmotionScenarioResponse {
        val statusUri = "${properties.emotionScenarioPath}/$jobId"
        repeat(properties.pollingMaxAttempts) { attempt ->
            Thread.sleep(properties.pollingIntervalMs)
            val status = restClient.get()
                .uri(statusUri)
                .exchange { _, response ->
                    objectMapper.readValue(
                        response.body.readAllBytes(),
                        AiScenarioStatusResponse::class.java
                    )
                } ?: return@repeat

            log.debug("[AiEmotionScenarioClient] 폴링 #{} — jobId={}, status={}", attempt + 1, jobId, status.status)

            when (status.status) {
                "done"  -> return status.result
                    ?: throw IllegalStateException("AI 시나리오 결과가 비어 있습니다. jobId=$jobId")
                "error" -> throw IllegalStateException("AI 시나리오 생성 실패. jobId=$jobId")
            }
        }
        throw IllegalStateException(
            "AI 시나리오 생성 타임아웃 (${properties.pollingMaxAttempts}회 × ${properties.pollingIntervalMs}ms). jobId=$jobId, memberId=$memberId"
        )
    }

    /**
     * AI 비활성화 시 반환할 최소 폴백 시나리오.
     * 로컬 개발 환경에서 AI 없이 전체 흐름을 테스트할 수 있도록 한다.
     */
    private fun buildFallbackScenario(request: AiEmotionScenarioRequest): AiEmotionScenarioResponse {
        val emotion = request.emotion ?: "기쁨"
        // 실제 폴백 시나리오는 사전 정의된 데이터로 교체 예정
        // 현재는 구조 확인용 최소 더미 반환
        throw UnsupportedOperationException(
            "로컬 개발 환경에서는 AI 폴백 시나리오가 미구현 상태입니다. " +
            "application.yml의 ai.service.enabled=true 로 설정 후 사용하거나 " +
            "사전 정의된 폴백 데이터를 추가하세요."
        )
    }
}
