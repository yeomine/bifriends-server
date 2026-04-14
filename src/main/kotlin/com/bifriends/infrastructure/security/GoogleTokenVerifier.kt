package com.bifriends.infrastructure.security

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleTokenVerifier(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}") private val clientId: String
) {

    private val verifier: GoogleIdTokenVerifier by lazy {
        GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(listOf(clientId))
            .build()
    }

    fun verify(idTokenString: String): GoogleIdToken.Payload {
        val idToken = verifier.verify(idTokenString)
            ?: throw IllegalArgumentException("유효하지 않은 Google ID 토큰입니다.")
        return idToken.payload
    }
}
