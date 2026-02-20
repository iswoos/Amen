package com.example.amen.domain.repository

import com.example.amen.domain.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    // 모든 기도 일기를 최신순으로 가져오기 (Flow 반환)
    fun getAllJournals(): Flow<List<JournalEntry>>
    
    // 특정 일기 저장하기
    suspend fun insertJournal(entry: JournalEntry)
    
    // 일기 삭제하기
    suspend fun deleteJournal(entry: JournalEntry)
}
