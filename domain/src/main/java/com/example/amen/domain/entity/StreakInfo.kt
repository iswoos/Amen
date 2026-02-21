package com.example.amen.domain.entity

data class StreakInfo(
    val currentStreakDays: Int,     // 현재 연속 출석 일수
    val lastVisitedDate: String?    // 마지막 방문 날짜 "YYYY-MM-DD", 초기엔 null
)
