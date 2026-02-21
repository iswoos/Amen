package com.example.amen.domain.repository

import com.example.amen.domain.entity.BibleVerse
import com.example.amen.domain.entity.DailyRoutineVerse
import kotlinx.coroutines.flow.Flow

interface BibleRepository {
    // 특정 날짜 기반의 오늘의 말씀 가져오기
    suspend fun getDailyRoutineVerse(dateString: String): DailyRoutineVerse

    // 성경 전체의 완독 진행률 (0.0 ~ 1.0) 반환
    suspend fun getBibleReadingProgress(): Float

    // 진도 업데이트 (특정 장을 읽었음을 표시)
    suspend fun markChapterAsRead(book: String, chapter: Int)

    suspend fun getAllBooks(): List<String>
    suspend fun getChapters(book: String): List<Int>
    suspend fun getVersesForChapter(book: String, chapter: Int): List<BibleVerse>

    // 좋아요 토글 (좋아요 ↔ 좋아요 취소)
    suspend fun toggleLike(verseId: Int)

    // 좋아요한 구절 목록 (실시간 갱신)
    fun getLikedVerses(): Flow<List<BibleVerse>>
}
