package com.example.amen.domain.entity

data class JournalEntry(
    val id: Long = 0,
    val dateTimestamp: Long,    // 작성일자 ms 단위
    val content: String,
    val mood: String? = null,   // 나중에 확장 가능한 기분 상태
    val verseRef: String? = null, // 참조 구절 (예: "창세기 1장 1절")
    val verseContent: String? = null // 참조 구절 내용
)
