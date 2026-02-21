package com.example.amen.data.audio

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
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
        // AudioFocus를 직접 요청하지 않고 USAGE_MEDIA로 설정
        // → TTS(System TTS)와 ExoPlayer가 AudioFocus를 공유하도록 처리
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(audioAttributes, false) // handleAudioFocus = false → 포커스 독점 안 함
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    // 로컬 assets 파일 재생
    override fun playAudio(uriString: String) {
        Log.d("AudioPlayerManager", "Playing audio: $uriString")
        val mediaItem = MediaItem.fromUri(Uri.parse(uriString))
        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // 배경음 볼륨 즉각 반영
    override fun setBackgroundVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    // 슬립 타이머: minutes 분 후 30초 페이드 아웃 후 정지
    override fun startSleepTimerWithFadeOut(minutes: Int) {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            // 설정한 시간(분) 대기
            delay(minutes * 60 * 1000L)

            // 30초에 걸쳐 페이드 아웃
            val fadeDurationMs = 30_000L
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

            // 페이드 아웃 완료 후 정지
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
