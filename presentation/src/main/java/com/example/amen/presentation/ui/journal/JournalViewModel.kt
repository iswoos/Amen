package com.example.amen.presentation.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.entity.JournalEntry
import com.example.amen.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    val journals: StateFlow<List<JournalEntry>> = journalRepository.getAllJournals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addJournal(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val entry = JournalEntry(
                dateTimestamp = System.currentTimeMillis(),
                content = content
            )
            journalRepository.insertJournal(entry)
        }
    }

    fun deleteJournal(entry: JournalEntry) {
        viewModelScope.launch {
            journalRepository.deleteJournal(entry)
        }
    }
}
