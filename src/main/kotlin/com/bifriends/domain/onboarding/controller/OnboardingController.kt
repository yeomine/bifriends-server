package com.bifriends.domain.onboarding.controller

import com.bifriends.domain.onboarding.dto.*
import com.bifriends.domain.onboarding.service.OnboardingService
import com.bifriends.infrastructure.security.JwtProvider
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/onboarding")
class OnboardingController(
    private val onboardingService: OnboardingService,
    private val jwtProvider: JwtProvider
) {

    @PostMapping("/guardian")
    fun saveGuardian(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: GuardianRequest
    ): ResponseEntity<GuardianResponse> {
        val memberId = extractMemberId(token)
        return ResponseEntity.ok(onboardingService.saveGuardianPhone(memberId, request))
    }

    @PatchMapping("/profile")
    fun updateProfile(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: ProfileRequest
    ): ResponseEntity<ProfileResponse> {
        val memberId = extractMemberId(token)
        return ResponseEntity.ok(onboardingService.updateProfile(memberId, request))
    }

    @PutMapping("/interests")
    fun saveInterests(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: InterestsRequest
    ): ResponseEntity<InterestsResponse> {
        val memberId = extractMemberId(token)
        return ResponseEntity.ok(onboardingService.saveInterests(memberId, request))
    }

    @PostMapping("/gift")
    fun saveGift(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: GiftRequest
    ): ResponseEntity<GiftResponse> {
        val memberId = extractMemberId(token)
        return ResponseEntity.ok(onboardingService.saveGift(memberId, request))
    }

    @PatchMapping("/permissions")
    fun updatePermissions(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody request: PermissionsRequest
    ): ResponseEntity<PermissionsResponse> {
        val memberId = extractMemberId(token)
        return ResponseEntity.ok(onboardingService.updatePermissions(memberId, request))
    }

    @PostMapping("/complete")
    fun completeOnboarding(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<OnboardingCompleteResponse> {
        val memberId = extractMemberId(token)
        return ResponseEntity.ok(onboardingService.completeOnboarding(memberId))
    }

    private fun extractMemberId(token: String): Long {
        val jwt = token.removePrefix("Bearer ")
        return jwtProvider.getMemberId(jwt)
    }
}
