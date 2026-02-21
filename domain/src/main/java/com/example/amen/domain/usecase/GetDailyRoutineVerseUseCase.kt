package com.example.amen.domain.usecase

import com.example.amen.domain.entity.DailyRoutineVerse
import com.example.amen.domain.repository.BibleRepository
import javax.inject.Inject

class GetDailyRoutineVerseUseCase @Inject constructor(
    private val bibleRepository: BibleRepository
) {
    suspend operator fun invoke(dateString: String): DailyRoutineVerse {
        return bibleRepository.getDailyRoutineVerse(dateString)
    }
}
