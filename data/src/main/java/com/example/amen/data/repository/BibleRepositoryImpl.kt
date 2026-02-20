package com.example.amen.data.repository

import com.example.amen.data.local.dao.BibleDao
import com.example.amen.domain.entity.DailyRoutineVerse
import com.example.amen.domain.repository.BibleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BibleRepositoryImpl @Inject constructor(
    private val bibleDao: BibleDao
) : BibleRepository {

    override suspend fun getDailyRoutineVerse(dateString: String): DailyRoutineVerse {
        // 실제 운영 시에는 dateString의 해시를 이용해 오늘의 구절을 고정시킬 수도 있습니다.
        // 여기선 가장 단순한 형태인 랜덤 구절 반환으로 우선 개발을 시작합니다.
        
        val verseEntity = bibleDao.getRandomVerse() 
            ?: throw IllegalStateException("Bible database is empty despite seeding attempt.")

        return DailyRoutineVerse(
            dateString = dateString,
            verse = verseEntity.toDomainModel()
        )
    }

    override suspend fun getBibleReadingProgress(): Float {
        val total = bibleDao.getTotalVersesCount()
        if (total == 0) return 0f
        
        val readCount = bibleDao.getReadVersesCount()
        return readCount.toFloat() / total.toFloat()
    }

    override suspend fun markChapterAsRead(book: String, chapter: Int) {
        withContext(Dispatchers.IO) {
            bibleDao.markChapterAsRead(book, chapter)
        }
    }
}
