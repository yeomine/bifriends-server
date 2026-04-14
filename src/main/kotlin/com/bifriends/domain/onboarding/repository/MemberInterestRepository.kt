package com.bifriends.domain.onboarding.repository

import com.bifriends.domain.onboarding.model.MemberInterest
import org.springframework.data.jpa.repository.JpaRepository

interface MemberInterestRepository : JpaRepository<MemberInterest, Long> {
    fun findAllByMemberId(memberId: Long): List<MemberInterest>
    fun deleteAllByMemberId(memberId: Long)
}
