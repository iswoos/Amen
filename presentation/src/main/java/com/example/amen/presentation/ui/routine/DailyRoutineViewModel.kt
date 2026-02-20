package com.example.amen.presentation.ui.routine

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.audio.TtsController
import com.example.amen.domain.entity.DailyRoutineVerse
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
class DailyRoutineViewModel @Inject constructor(
    private val getDailyRoutineVerseUseCase: GetDailyRoutineVerseUseCase,
    private val ttsController: TtsController
) : ViewModel() {

    private val _verseState = MutableStateFlow<DailyRoutineVerse?>(null)
    val verseState: StateFlow<DailyRoutineVerse?> = _verseState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTodayVerse() {
        viewModelScope.launch {
            try {
                val todayString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val routineVerse = getDailyRoutineVerseUseCase(todayString)
                _verseState.value = routineVerse
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleTtsPlayback() {
        val currentVerse = _verseState.value?.verse?.content ?: return
        if (_isPlaying.value) {
            ttsController.stop()
            _isPlaying.value = false
        } else {
            ttsController.speak(currentVerse)
            _isPlaying.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsController.shutdown()
    }
}
