package com.example.amen.presentation.ui.relax

import androidx.lifecycle.ViewModel
import com.example.amen.domain.audio.AudioController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RelaxViewModel @Inject constructor(
    private val audioController: AudioController
) : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    // 향후 Assets이나 서버 URI에서 받아올 임시 함수
    fun togglePlayStatus() {
        if (_isPlaying.value) {
            audioController.stop()
            _isPlaying.value = false
        } else {
            // 더미 리소스 대신 플레이어 세팅만 활성화(에셋이 없으므로 일단 상태만 변경)
            // audioController.playAudio(...)
            _isPlaying.value = true
        }
    }

    fun setVolume(newVolume: Float) {
        _volume.value = newVolume
        audioController.setVolume(newVolume)
    }

    fun startSleepTimer(minutes: Int) {
        audioController.startSleepTimerWithFadeOut(minutes)
    }

    override fun onCleared() {
        super.onCleared()
        audioController.release()
    }
}
