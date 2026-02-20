package com.example.amen.presentation.ui.card

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.amen.domain.entity.BibleVerse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CardShareViewModel @Inject constructor() : ViewModel() {

    private val _currentVerse = MutableStateFlow<BibleVerse?>(null)
    val currentVerse: StateFlow<BibleVerse?> = _currentVerse.asStateFlow()

    fun setVerseToShare(verse: BibleVerse) {
        _currentVerse.value = verse
    }

    fun shareVerseText(context: Context) {
        val verse = _currentVerse.value ?: return
        val shareText = "🙏 오늘의 은혜로운 말씀\n\n\"${verse.content}\"\n- ${verse.book} ${verse.chapter}:${verse.verse} -"
        
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "말씀 공유하기")
        context.startActivity(shareIntent)
    }
}
