package com.example.amen.domain.entity

data class BibleVerse(
    val id: Int,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val content: String,
    val isLiked: Boolean = false // 좋아요 여부
)

data class DailyRoutineVerse(
    val dateString: String, // format: "YYYY-MM-DD"
    val verse: BibleVerse
)
