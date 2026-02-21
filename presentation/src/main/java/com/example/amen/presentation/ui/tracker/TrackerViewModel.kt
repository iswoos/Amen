package com.example.amen.presentation.ui.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    init {
        loadReadingProgress()
    }

    private fun loadReadingProgress() {
        viewModelScope.launch {
            try {
                _progress.value = bibleRepository.getBibleReadingProgress()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
