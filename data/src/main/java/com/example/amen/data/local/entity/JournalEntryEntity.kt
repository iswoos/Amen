package com.example.amen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.amen.domain.entity.JournalEntry

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateTimestamp: Long,
    val content: String,
    val mood: String?,
    val verseRef: String? = null, // 참조 구절 (예: "창세기 1장 1절")
    val verseContent: String? = null // 참조 구절 내용
) {
    fun toDomainModel(): JournalEntry {
        return JournalEntry(
            id = id,
            dateTimestamp = dateTimestamp,
            content = content,
            mood = mood,
            verseRef = verseRef,
            verseContent = verseContent
        )
    }

    companion object {
        fun fromDomainModel(entry: JournalEntry): JournalEntryEntity {
            return JournalEntryEntity(
                id = entry.id,
                dateTimestamp = entry.dateTimestamp,
                content = entry.content,
                mood = entry.mood,
                verseRef = entry.verseRef,
                verseContent = entry.verseContent
            )
        }
    }
}
