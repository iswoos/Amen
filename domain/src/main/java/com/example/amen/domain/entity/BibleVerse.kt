package com.example.amen.domain.entity

data class BibleVerse(
    val id: Int,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val content: String
)

data class DailyRoutineVerse(
    val dateString: String, // format: "YYYY-MM-DD"
    val verse: BibleVerse
)
