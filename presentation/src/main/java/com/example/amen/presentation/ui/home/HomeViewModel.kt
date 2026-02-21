package com.example.amen.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.audio.TtsController
import com.example.amen.domain.entity.BibleVerse
import com.example.amen.domain.entity.JournalEntry
import com.example.amen.domain.repository.BibleRepository
import com.example.amen.domain.repository.JournalRepository
import com.example.amen.domain.usecase.GetDailyRoutineVerseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val journalRepository: JournalRepository,
    private val getDailyRoutineVerseUseCase: GetDailyRoutineVerseUseCase,
    private val ttsController: TtsController
) : ViewModel() {

    private val _dailyVerse = MutableStateFlow<BibleVerse?>(null)
    val dailyVerse: StateFlow<BibleVerse?> = _dailyVerse.asStateFlow()

    private val _isTtsPlaying = MutableStateFlow(false)
    val isTtsPlaying: StateFlow<Boolean> = _isTtsPlaying.asStateFlow()

    private val _readingProgress = MutableStateFlow(0f)
    val readingProgress: StateFlow<Float> = _readingProgress.asStateFlow()

    init {
        loadTodayVerse()
        loadReadingProgress()
        ttsController.setOnCompletionListener {
            _isTtsPlaying.value = false
        }
    }

    private fun loadTodayVerse() {
        viewModelScope.launch {
            try {
                val todayString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val routineVerse = getDailyRoutineVerseUseCase(todayString)
                _dailyVerse.value = routineVerse.verse
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadReadingProgress() {
        viewModelScope.launch {
            _readingProgress.value = bibleRepository.getBibleReadingProgress()
        }
    }

    fun toggleLike() {
        val verse = _dailyVerse.value ?: return
        viewModelScope.launch {
            bibleRepository.toggleLike(verse.id)
            // 반전된 상태를 로컬 상태에도 즉시 반영 (혹은 Flow 구독으로 처리 가능하지만 간단히 수동 갱신)
            _dailyVerse.value = verse.copy(isLiked = !verse.isLiked)
        }
    }

    fun toggleTts() {
        val content = _dailyVerse.value?.content ?: return
        if (_isTtsPlaying.value) {
            ttsController.stop()
            _isTtsPlaying.value = false
        } else {
            ttsController.speak(content)
            _isTtsPlaying.value = true
        }
    }

    fun saveJournal(content: String) {
        if (content.isBlank()) return
        
        val verse = _dailyVerse.value
        val verseRef = if (verse != null) "${verse.book} ${verse.chapter}장 ${verse.verse}절" else null
        val verseContent = verse?.content
        
        viewModelScope.launch {
            val entry = JournalEntry(
                dateTimestamp = System.currentTimeMillis(),
                content = content,
                verseRef = verseRef,
                verseContent = verseContent
            )
            journalRepository.insertJournal(entry)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsController.stop()
    }
}
