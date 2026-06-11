package com.bifriends.infrastructure.ai

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestClient

@Configuration
@EnableConfigurationProperties(AiServiceProperties::class)
class AiClientConfig {

    @Bean
    fun aiRestClient(properties: AiServiceProperties): RestClient {
        val factory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(10_000)
            setReadTimeout(properties.readTimeoutMs)
        }
        // AI 서버가 Content-Type: application/octet-stream으로 응답하는 경우에도 JSON 파싱
        val jsonConverter = MappingJackson2HttpMessageConverter().apply {
            supportedMediaTypes = listOf(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
        }
        return RestClient.builder()
            .baseUrl(properties.baseUrl)
            .requestFactory(factory)
            .messageConverters { it.add(0, jsonConverter) }
            .build()
    }
}
