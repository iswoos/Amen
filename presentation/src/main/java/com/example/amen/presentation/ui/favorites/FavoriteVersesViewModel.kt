package com.example.amen.presentation.ui.favorites

import androidx.lifecycle.ViewModel
import com.example.amen.domain.entity.BibleVerse
import com.example.amen.domain.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope

@HiltViewModel
class FavoriteVersesViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    val likedVerses: StateFlow<List<BibleVerse>> = bibleRepository.getLikedVerses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleLike(verseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleRepository.toggleLike(verseId)
        }
    }
}
