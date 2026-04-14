package com.bifriends.domain.onboarding.model

import com.bifriends.domain.member.model.Member
import jakarta.persistence.*

@Entity
@Table(name = "member_interests")
class MemberInterest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val interest: Interest
)
