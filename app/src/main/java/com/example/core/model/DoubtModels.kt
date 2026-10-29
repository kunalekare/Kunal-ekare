package com.example.core.model

data class DoubtReply(
    val id: String,
    val authorName: String,
    val authorRole: String, // "Faculty", "Senior Mentor", "Peer"
    val content: String,
    val timestampFormatted: String,
    val isVerifiedSolution: Boolean = false,
    val upvotes: Int = 0
)

data class DoubtItem(
    val id: String,
    val studentName: String,
    val title: String,
    val description: String,
    val subject: String,
    val level: StudentLevel,
    val hasImage: Boolean = false,
    val imageUrl: String? = null,
    val upvotesCount: Int = 12,
    val repliesCount: Int = 2,
    val isResolved: Boolean = false,
    val replies: List<DoubtReply> = emptyList()
)
