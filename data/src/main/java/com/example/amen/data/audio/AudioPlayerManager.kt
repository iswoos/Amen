package com.example.amen.data.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.amen.domain.audio.AudioController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioController {
    private var exoPlayer: ExoPlayer? = null
    private var fadeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    // 로컬 raw 리소스 등에서 uri를 받아 재생 (에셋 리소스가 추가되면 사용)
    override fun playAudio(uriString: String) {
        val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }
    
    // 오디오 볼륨 직접 설정
    override fun setVolume(volume: Float) {
        exoPlayer?.volume = volume
    }

    // 슬립 타이머(지정된 시간 후 30초에 걸쳐 Fade Out 하면서 정지)
    override fun startSleepTimerWithFadeOut(minutes: Int) {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            // 대기
            delay(minutes * 60 * 1000L)
            
            // 30초 Fade Out (1초마다 볼륨을 서서히 줄임)
            val fadeDurationMs = 30000L
            val initialVolume = exoPlayer?.volume ?: 1.0f
            val steps = 30
            val delayPerStep = fadeDurationMs / steps
            val volumeAdjustment = initialVolume / steps

            for (i in 0 until steps) {
                val currentVolume = exoPlayer?.volume ?: break
                val nextVolume = (currentVolume - volumeAdjustment).coerceAtLeast(0f)
                exoPlayer?.volume = nextVolume
                delay(delayPerStep)
            }

            // 페이드아웃 완료 후 중지
            stop()
        }
    }

    override fun stop() {
        fadeJob?.cancel()
        exoPlayer?.stop()
        exoPlayer?.volume = 1f // 다음 재생을 위해 볼륨 리셋
    }

    override fun release() {
        fadeJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
    }
}
