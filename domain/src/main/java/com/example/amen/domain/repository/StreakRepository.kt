package com.example.amen.domain.repository

import com.example.amen.domain.entity.StreakInfo
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    // 실시간 달성 스트릭 데이터 관찰 
    fun getStreakInfo(): Flow<StreakInfo>

    // 앱 방문 시 오늘 날짜의 출석을 기록하고 스트릭 갱신
    suspend fun recordVisitAndGetStreak(todayDateString: String): StreakInfo
}
