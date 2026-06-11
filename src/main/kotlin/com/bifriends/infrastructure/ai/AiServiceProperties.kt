package com.bifriends.infrastructure.ai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ai.service")
data class AiServiceProperties(
    val enabled: Boolean = false,
    val baseUrl: String = "http://bifriends-ai:8001",
    /** AI 채팅 엔드포인트 경로 */
    val chatPath: String = "/v1/chat",
    /** BE 스케줄러 → AI 주간 안전 보고서 배치 트리거 경로 */
    val batchWeeklySafetyPath: String = "/api/v1/ai/batch/weekly-safety",
    /** BE 스케줄러 → AI 주간 성장 리포트(부모용) 배치 트리거 경로 */
    val batchWeeklyReportPath: String = "/api/v1/ai/report/weekly",
    /** 친구랑 감정 학습 시나리오 생성 요청 경로 (EMO-04) */
    val emotionScenarioPath: String = "/api/v1/ai/content/scenario",
    /** 시나리오 job 상태 폴링 간격 (ms) */
    val pollingIntervalMs: Long = 2000,
    /** 폴링 최대 횟수 (초과 시 타임아웃) */
    val pollingMaxAttempts: Int = 60,
    /** AI 서버 HTTP read timeout (ms) — Gemini 재시도 포함 시간 고려 */
    val readTimeoutMs: Int = 120_000,
)
