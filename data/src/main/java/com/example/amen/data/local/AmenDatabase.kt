package com.example.amen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.amen.data.local.dao.BibleDao
import com.example.amen.data.local.dao.JournalDao
import com.example.amen.data.local.entity.BibleVerseEntity
import com.example.amen.data.local.entity.JournalEntryEntity

@Database(
    entities = [BibleVerseEntity::class, JournalEntryEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AmenDatabase : RoomDatabase() {
    abstract val bibleDao: BibleDao
    abstract val journalDao: JournalDao

    companion object {
        const val DATABASE_NAME = "amen_db"

        // ver 2 → 3: bible_verses에 isLiked, journal_entries에 verseRef 추가
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE bible_verses ADD COLUMN isLiked INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE journal_entries ADD COLUMN verseRef TEXT")
            }
        }

        // ver 3 → 4: journal_entries에 verseContent 추가
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE journal_entries ADD COLUMN verseContent TEXT")
            }
        }
    }
}
