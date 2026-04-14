package com.bifriends.domain.onboarding.service

import com.bifriends.domain.member.repository.MemberRepository
import com.bifriends.domain.onboarding.dto.*
import com.bifriends.domain.onboarding.model.Interest
import com.bifriends.domain.onboarding.model.ItemType
import com.bifriends.domain.onboarding.model.MemberInterest
import com.bifriends.domain.onboarding.model.MemberItem
import com.bifriends.domain.onboarding.repository.MemberInterestRepository
import com.bifriends.domain.onboarding.repository.MemberItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OnboardingService(
    private val memberRepository: MemberRepository,
    private val memberInterestRepository: MemberInterestRepository,
    private val memberItemRepository: MemberItemRepository
) {

    @Transactional
    fun saveGuardianPhone(memberId: Long, request: GuardianRequest): GuardianResponse {
        val member = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("회원을 찾을 수 없습니다. id=$memberId") }
        member.guardianPhone = request.phoneNumber
        return GuardianResponse(verified = true)
    }

    @Transactional
    fun updateProfile(memberId: Long, request: ProfileRequest): ProfileResponse {
        val member = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("회원을 찾을 수 없습니다. id=$memberId") }
        request.nickname?.let { member.nickname = it }
        request.grade?.let { member.grade = it }
        return ProfileResponse(nickname = member.nickname, grade = member.grade)
    }

    @Transactional
    fun saveInterests(memberId: Long, request: InterestsRequest): InterestsResponse {
        val member = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("회원을 찾을 수 없습니다. id=$memberId") }

        memberInterestRepository.deleteAllByMemberId(memberId)
        memberInterestRepository.flush()

        val interests = request.interests.distinct().map { interest ->
            MemberInterest(member = member, interest = interest)
        }
        memberInterestRepository.saveAll(interests)

        return InterestsResponse(interests = request.interests.distinct())
    }

    @Transactional
    fun saveGift(memberId: Long, request: GiftRequest): GiftResponse {
        val member = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("회원을 찾을 수 없습니다. id=$memberId") }

        val item = memberItemRepository.save(
            MemberItem(member = member, itemType = request.itemType)
        )

        return GiftResponse(itemType = item.itemType, acquiredAt = item.acquiredAt.toString())
    }

    @Transactional
    fun updatePermissions(memberId: Long, request: PermissionsRequest): PermissionsResponse {
        val member = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("회원을 찾을 수 없습니다. id=$memberId") }
        member.notificationEnabled = request.notificationEnabled
        member.microphoneEnabled = request.microphoneEnabled
        return PermissionsResponse(
            notificationEnabled = member.notificationEnabled,
            microphoneEnabled = member.microphoneEnabled
        )
    }

    @Transactional
    fun completeOnboarding(memberId: Long): OnboardingCompleteResponse {
        val member = memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("회원을 찾을 수 없습니다. id=$memberId") }
        member.onboardingCompleted = true
        return OnboardingCompleteResponse(completed = true)
    }
}
