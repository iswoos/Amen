package com.example.amen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.amen.data.local.entity.BibleVerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    // 성경 구절 대량 삽입 (초기화용)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<BibleVerseEntity>)

    // 임의의 말씀 1개 가져오기
    @Query("SELECT * FROM bible_verses ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVerse(): BibleVerseEntity?

    @Query("SELECT * FROM bible_verses WHERE id = :verseId LIMIT 1")
    suspend fun getVerseById(verseId: Int): BibleVerseEntity?

    // 완독한 구절 수 가져오기
    @Query("SELECT COUNT(*) FROM bible_verses WHERE isRead = 1")
    suspend fun getReadVersesCount(): Int

    // 전체 구절 수 가져오기
    @Query("SELECT COUNT(*) FROM bible_verses")
    suspend fun getTotalVersesCount(): Int

    // 특정 장을 모두 읽음 처리
    @Query("UPDATE bible_verses SET isRead = 1 WHERE book = :book AND chapter = :chapter")
    suspend fun markChapterAsRead(book: String, chapter: Int)

    // 성경 권 목록 가져오기 (고유값)
    @Query("SELECT DISTINCT book FROM bible_verses")
    suspend fun getAllBooks(): List<String>

    // 특정 권의 장 목록 가져오기
    @Query("SELECT DISTINCT chapter FROM bible_verses WHERE book = :book ORDER BY chapter ASC")
    suspend fun getChapters(book: String): List<Int>

    // 특정 장의 모든 절 가져오기
    @Query("SELECT * FROM bible_verses WHERE book = :book AND chapter = :chapter ORDER BY verse ASC")
    suspend fun getVersesForChapter(book: String, chapter: Int): List<BibleVerseEntity>

    // 좋아요한 구절 목록 (실시간 Flow)
    @Query("SELECT * FROM bible_verses WHERE isLiked = 1 ORDER BY book, chapter, verse")
    fun getLikedVerses(): Flow<List<BibleVerseEntity>>

    // 좋아요 토글
    @Query("UPDATE bible_verses SET isLiked = CASE WHEN isLiked = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleLike(id: Int)

    @Query("DELETE FROM bible_verses")
    suspend fun deleteAllVerses()
}
