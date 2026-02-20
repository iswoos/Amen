package com.example.amen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.amen.data.local.dao.BibleDao
import com.example.amen.data.local.dao.JournalDao
import com.example.amen.data.local.entity.BibleVerseEntity
import com.example.amen.data.local.entity.JournalEntryEntity

@Database(
    entities = [BibleVerseEntity::class, JournalEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AmenDatabase : RoomDatabase() {
    abstract val bibleDao: BibleDao
    abstract val journalDao: JournalDao

    companion object {
        const val DATABASE_NAME = "amen_db"
    }
}
