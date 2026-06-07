package com.bifriends.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 및 검증 유틸.
 * - accessToken: 인증용 (1시간)
 * - refreshToken: 재발급용 (7일)
 */
@Component
class JwtProvider(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long
) {

    private val log = LoggerFactory.getLogger(JwtProvider::class.java)

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateAccessToken(memberId: Long, email: String, role: String): String {
        return generateToken(memberId, email, role, accessTokenExpiration)
    }

    fun generateRefreshToken(memberId: Long, email: String, role: String): String {
        return generateToken(memberId, email, role, refreshTokenExpiration)
    }

    private fun generateToken(memberId: Long, email: String, role: String, expiration: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .subject(memberId.toString())
            .claim("email", email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: Exception) {
            log.warn("JWT validation failed: [{}] {}", e.javaClass.simpleName, e.message)
            false
        }
    }

    fun getMemberId(token: String): Long {
        return getClaims(token).subject.toLong()
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
