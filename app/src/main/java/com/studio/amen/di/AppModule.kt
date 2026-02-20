package com.studio.amen.di

import android.content.Context
import androidx.room.Room
import com.example.amen.data.audio.AudioPlayerManager
import com.example.amen.data.audio.TtsManager
import com.example.amen.data.local.AmenDatabase
import com.example.amen.data.local.dao.BibleDao
import com.example.amen.data.local.dao.JournalDao
import com.example.amen.data.repository.BibleRepositoryImpl
import com.example.amen.data.repository.JournalRepositoryImpl
import com.example.amen.data.repository.StreakRepositoryImpl
import com.example.amen.domain.audio.AudioController
import com.example.amen.domain.audio.TtsController
import com.example.amen.domain.repository.BibleRepository
import com.example.amen.domain.repository.JournalRepository
import com.example.amen.domain.repository.StreakRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAmenDatabase(@ApplicationContext context: Context): AmenDatabase {
        return Room.databaseBuilder(
            context,
            AmenDatabase::class.java,
            AmenDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideBibleDao(database: AmenDatabase): BibleDao = database.bibleDao

    @Provides
    fun provideJournalDao(database: AmenDatabase): JournalDao = database.journalDao

    @Provides
    @Singleton
    fun provideTtsController(@ApplicationContext context: Context): TtsController {
        return TtsManager(context)
    }

    @Provides
    @Singleton
    fun provideAudioController(@ApplicationContext context: Context): AudioController {
        return AudioPlayerManager(context)
    }

    @Provides
    @Singleton
    fun provideBibleRepository(
        bibleDao: BibleDao
    ): BibleRepository {
        return BibleRepositoryImpl(bibleDao)
    }

    @Provides
    @Singleton
    fun provideJournalRepository(
        journalDao: JournalDao
    ): JournalRepository {
        return JournalRepositoryImpl(journalDao)
    }

    @Provides
    @Singleton
    fun provideStreakRepository(
        @ApplicationContext context: Context
    ): StreakRepository {
        return StreakRepositoryImpl(context)
    }
}
