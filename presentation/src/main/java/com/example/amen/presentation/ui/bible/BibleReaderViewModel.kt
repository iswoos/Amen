package com.example.amen.presentation.ui.bible

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.entity.BibleVerse
import com.example.amen.domain.repository.BibleRepository
import com.example.amen.domain.audio.TtsController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleReaderViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val ttsController: TtsController
) : ViewModel() {

    private val _verses = MutableStateFlow<List<BibleVerse>>(emptyList())
    val verses: StateFlow<List<BibleVerse>> = _verses.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    init {
        ttsController.setOnCompletionListener {
            _isPlaying.value = false
        }
    }

    fun loadChapter(book: String, chapter: Int) {
        _title.value = "$book ${chapter}장"
        viewModelScope.launch {
            _verses.value = bibleRepository.getVersesForChapter(book, chapter)
        }
    }

    fun toggleTts() {
        if (_isPlaying.value) {
            stopPlayback()
        } else {
            startPlayback(0)
        }
    }

    private fun startPlayback(startIndex: Int) {
        val texts = _verses.value.map { "${it.verse}절. ${it.content}" }
        if (texts.isNotEmpty()) {
            ttsController.speakFromIndex(texts, startIndex)
            _isPlaying.value = true
        }
    }

    private fun stopPlayback() {
        ttsController.stop()
        _isPlaying.value = false
    }

    fun playFromVerse(index: Int) {
        stopPlayback()
        startPlayback(index)
    }

    override fun onCleared() {
        super.onCleared()
        ttsController.stop()
    }
}
