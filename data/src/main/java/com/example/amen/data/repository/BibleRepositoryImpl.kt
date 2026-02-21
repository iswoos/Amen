package com.example.amen.data.repository

import com.example.amen.data.local.dao.BibleDao
import com.example.amen.domain.entity.BibleVerse
import com.example.amen.domain.entity.DailyRoutineVerse
import com.example.amen.domain.repository.BibleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

import com.example.amen.data.local.util.BibleDataSeeder

class BibleRepositoryImpl @Inject constructor(
    private val bibleDao: BibleDao,
    private val bibleDataSeeder: BibleDataSeeder
) : BibleRepository {

    override suspend fun getDailyRoutineVerse(dateString: String): DailyRoutineVerse {
        bibleDataSeeder.seedIfNeeded()
        val verseEntity = bibleDao.getRandomVerse()
            ?: throw IllegalStateException("Bible database is empty despite seeding attempt.")
        return DailyRoutineVerse(dateString = dateString, verse = verseEntity.toDomainModel())
    }

    override suspend fun getBibleReadingProgress(): Float {
        val total = bibleDao.getTotalVersesCount()
        if (total == 0) return 0f
        val readCount = bibleDao.getReadVersesCount()
        return readCount.toFloat() / total.toFloat()
    }

    override suspend fun markChapterAsRead(book: String, chapter: Int) {
        withContext(Dispatchers.IO) { bibleDao.markChapterAsRead(book, chapter) }
    }

    override suspend fun getAllBooks(): List<String> = withContext(Dispatchers.IO) {
        bibleDao.getAllBooks()
    }

    override suspend fun getChapters(book: String): List<Int> = withContext(Dispatchers.IO) {
        bibleDao.getChapters(book)
    }

    override suspend fun getVersesForChapter(book: String, chapter: Int): List<BibleVerse> =
        withContext(Dispatchers.IO) {
            bibleDao.getVersesForChapter(book, chapter).map { it.toDomainModel() }
        }

    override suspend fun toggleLike(verseId: Int) {
        withContext(Dispatchers.IO) { bibleDao.toggleLike(verseId) }
    }

    override fun getLikedVerses(): Flow<List<BibleVerse>> {
        return bibleDao.getLikedVerses().map { entities -> entities.map { it.toDomainModel() } }
    }
}
