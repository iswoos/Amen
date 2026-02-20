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

    // 임의의 말씀 1개 가져오기 (초기 하드코딩된 더미 로직용/혹은 진짜 랜덤 구절용)
    @Query("SELECT * FROM bible_verses ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVerse(): BibleVerseEntity?

    @Query("SELECT * FROM bible_verses WHERE id = :verseId LIMIT 1")
    suspend fun getVerseById(verseId: Int): BibleVerseEntity?

    // 완독한 구절 수 가져오기 (전체 완독률 계산용)
    @Query("SELECT COUNT(*) FROM bible_verses WHERE isRead = 1")
    suspend fun getReadVersesCount(): Int

    // 전체 구절 수 가져오기 (31102 등)
    @Query("SELECT COUNT(*) FROM bible_verses")
    suspend fun getTotalVersesCount(): Int

    // 특정 장을 모두 읽음 처리
    @Query("UPDATE bible_verses SET isRead = 1 WHERE book = :book AND chapter = :chapter")
    suspend fun markChapterAsRead(book: String, chapter: Int)
}
