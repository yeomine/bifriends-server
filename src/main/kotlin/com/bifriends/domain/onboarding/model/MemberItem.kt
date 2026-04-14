package com.bifriends.domain.onboarding.model

import com.bifriends.domain.member.model.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_items")
class MemberItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val itemType: ItemType,

    @Column(nullable = false)
    val acquiredAt: LocalDateTime = LocalDateTime.now()
)
