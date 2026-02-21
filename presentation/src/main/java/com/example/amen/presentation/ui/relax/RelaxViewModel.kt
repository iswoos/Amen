package com.example.amen.presentation.ui.relax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.audio.AudioController
import com.example.amen.domain.audio.TtsController
import com.example.amen.domain.entity.BibleVerse
import com.example.amen.domain.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelaxViewModel @Inject constructor(
    private val audioController: AudioController,
    private val ttsController: TtsController,
    private val bibleRepository: BibleRepository
) : ViewModel() {

    // ── 재생 상태 ──────────────────────────────────────────────────────
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // ── 볼륨 (배경음 / 낭독 분리) ─────────────────────────────────────
    private val _backgroundVolume = MutableStateFlow(0.5f)
    val backgroundVolume: StateFlow<Float> = _backgroundVolume.asStateFlow()

    private val _ttsVolume = MutableStateFlow(1.0f)
    val ttsVolume: StateFlow<Float> = _ttsVolume.asStateFlow()

    // ── 성경 선택 ──────────────────────────────────────────────────────
    private val _books = MutableStateFlow<List<String>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredBooks: StateFlow<List<String>> = combine(_books, _searchQuery) { books, query ->
        if (query.isBlank()) books
        else books.filter { it.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chapters = MutableStateFlow<List<Int>>(emptyList())
    val chapters: StateFlow<List<Int>> = _chapters.asStateFlow()

    private val _selectedBook = MutableStateFlow<String?>(null)
    val selectedBook: StateFlow<String?> = _selectedBook.asStateFlow()

    private val _selectedChapter = MutableStateFlow<Int?>(null)
    val selectedChapter: StateFlow<Int?> = _selectedChapter.asStateFlow()

    // ── 선택된 장의 구절 (장 선택 즉시 로드) ─────────────────────────
    private val _selectedVerses = MutableStateFlow<List<BibleVerse>>(emptyList())
    val selectedVerses: StateFlow<List<BibleVerse>> = _selectedVerses.asStateFlow()

    // ── 슬립 타이머 ────────────────────────────────────────────────────
    private val _sleepTimerActive = MutableStateFlow(false)
    val sleepTimerActive: StateFlow<Boolean> = _sleepTimerActive.asStateFlow()

    /** 슬립 타이머 남은 시간 (초). -1 = 비활성 */
    private val _sleepTimerRemainingSecs = MutableStateFlow(-1L)
    val sleepTimerRemainingSecs: StateFlow<Long> = _sleepTimerRemainingSecs.asStateFlow()

    private var sleepTimerCountdownJob: Job? = null

    init {
        loadBooks()
        // 초기 볼륨 적용
        audioController.setBackgroundVolume(_backgroundVolume.value)
        ttsController.setVolume(_ttsVolume.value)
        
        ttsController.setOnCompletionListener {
            _isPlaying.value = false
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _books.value = bibleRepository.getAllBooks()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectBook(book: String) {
        _selectedBook.value = book
        _selectedChapter.value = null
        _selectedVerses.value = emptyList()
        _searchQuery.value = "" // 선택 후 검색어 초기화
        viewModelScope.launch {
            _chapters.value = bibleRepository.getChapters(book)
        }
    }

    fun selectChapter(chapter: Int) {
        _selectedChapter.value = chapter
        val book = _selectedBook.value ?: return
        viewModelScope.launch {
            _selectedVerses.value = bibleRepository.getVersesForChapter(book, chapter)
        }
    }

    // ── 재생 토글 및 구절 재생 ──────────────────────────────────────────
    fun togglePlayStatus() {
        if (_isPlaying.value) {
            stopPlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback(startIndex: Int = 0) {
        _isPlaying.value = true
        // 배경음 재생
        audioController.playAudio("file:///android_asset/audio/gregorian_chant.mp3")
        audioController.setBackgroundVolume(_backgroundVolume.value)

        // TTS 낭독 시작
        val verses = _selectedVerses.value
        if (verses.isNotEmpty()) {
            val texts = verses.map { it.content }
            ttsController.speakFromIndex(texts, startIndex)
        }
    }

    private fun stopPlayback() {
        audioController.stop()
        ttsController.stop()
        _isPlaying.value = false
    }

    /** 특정 절 클릭 시 해당 절부터 낭독 */
    fun playFromVerse(index: Int) {
        stopPlayback()
        startPlayback(startIndex = index)
    }

    // ── 볼륨 조절 ─────────────────────────────────────────────────────
    fun setBackgroundVolume(volume: Float) {
        _backgroundVolume.value = volume
        audioController.setBackgroundVolume(volume)
    }

    fun setTtsVolume(volume: Float) {
        _ttsVolume.value = volume
        ttsController.setVolume(volume)
        // Note: TtsManager에서 구절 단위로 speak를 호출하므로 다음 구절부터 반영됨
    }

    // ── 슬립 타이머 ────────────────────────────────────────────────────
    fun startSleepTimer(minutes: Int) {
        if (minutes <= 0) return
        
        sleepTimerCountdownJob?.cancel()
        _sleepTimerActive.value = true
        val totalSecs = minutes * 60L
        _sleepTimerRemainingSecs.value = totalSecs

        sleepTimerCountdownJob = viewModelScope.launch {
            var remaining = totalSecs
            while (remaining > 0) {
                delay(1000L)
                remaining--
                _sleepTimerRemainingSecs.value = remaining
            }
            _sleepTimerActive.value = false
            _sleepTimerRemainingSecs.value = -1L
            stopPlayback()
        }

        audioController.startSleepTimerWithFadeOut(minutes)
    }

    fun cancelSleepTimer() {
        sleepTimerCountdownJob?.cancel()
        _sleepTimerActive.value = false
        _sleepTimerRemainingSecs.value = -1L
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
        sleepTimerCountdownJob?.cancel()
    }
}
