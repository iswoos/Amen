package com.example.amen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.amen.domain.entity.BibleVerse

@Entity(tableName = "bible_verses")
data class BibleVerseEntity(
    @PrimaryKey
    val id: Int,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val content: String,
    val isRead: Boolean = false // 완독 트래커를 위한 컬럼
) {
    fun toDomainModel(): BibleVerse {
        return BibleVerse(
            id = id,
            book = book,
            chapter = chapter,
            verse = verse,
            content = content
        )
    }
}
