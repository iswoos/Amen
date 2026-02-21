package com.example.amen.data.repository

import com.example.amen.data.local.dao.JournalDao
import com.example.amen.data.local.entity.JournalEntryEntity
import com.example.amen.domain.entity.JournalEntry
import com.example.amen.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : JournalRepository {

    override fun getAllJournals(): Flow<List<JournalEntry>> {
        return journalDao.getAllJournals().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertJournal(entry: JournalEntry) {
        journalDao.insertJournal(JournalEntryEntity.fromDomainModel(entry))
    }

    override suspend fun deleteJournal(entry: JournalEntry) {
        journalDao.deleteJournal(JournalEntryEntity.fromDomainModel(entry))
    }
}
