package com.example.amen.domain.usecase

import com.example.amen.domain.entity.StreakInfo
import com.example.amen.domain.repository.StreakRepository
import javax.inject.Inject

class RecordVisitUseCase @Inject constructor(
    private val streakRepository: StreakRepository
) {
    suspend operator fun invoke(todayDateString: String): StreakInfo {
        return streakRepository.recordVisitAndGetStreak(todayDateString)
    }
}
