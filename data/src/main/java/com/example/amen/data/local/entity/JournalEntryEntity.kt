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
    val mood: String?
) {
    fun toDomainModel(): JournalEntry {
        return JournalEntry(
            id = id,
            dateTimestamp = dateTimestamp,
            content = content,
            mood = mood
        )
    }

    companion object {
        fun fromDomainModel(entry: JournalEntry): JournalEntryEntity {
            return JournalEntryEntity(
                id = entry.id,
                dateTimestamp = entry.dateTimestamp,
                content = entry.content,
                mood = entry.mood
            )
        }
    }
}
