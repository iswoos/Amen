package com.example.amen.data.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.amen.domain.audio.TtsController
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener, TtsController {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var onCompletionListener: (() -> Unit)? = null

    private var currentVolume: Float = 1.0f

    private val progressListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            Log.d("TtsManager", "Utterance started: $utteranceId")
        }
        override fun onDone(utteranceId: String?) {
            if (utteranceId?.startsWith("LAST_") == true) {
                onCompletionListener?.invoke()
            }
        }
        override fun onError(utteranceId: String?) {
            onCompletionListener?.invoke()
        }
    }

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = false
            } else {
                tts?.setPitch(0.85f)
                tts?.setSpeechRate(0.85f)
                tts?.setOnUtteranceProgressListener(progressListener)
                isInitialized = true
            }
        }
    }

    override fun speak(text: String) {
        if (!isInitialized) return

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, currentVolume.coerceIn(0f, 1f))
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "LAST_${System.currentTimeMillis()}")
    }

    override fun speakFromIndex(verses: List<String>, startIndex: Int) {
        if (!isInitialized || verses.isEmpty()) return

        tts?.stop() // 기존 낭독 중단

        for (i in startIndex until verses.size) {
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, currentVolume.coerceIn(0f, 1f))
            }
            val queueMode = if (i == startIndex) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            val utteranceId = if (i == verses.size - 1) "LAST_${i}" else "ID_${i}"
            
            tts?.speak(verses[i], queueMode, params, utteranceId)
        }
    }

    override fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        // Note: Real-time update for the CURRENTLY speaking utterance is not supported by Android TTS API.
        // However, by using speakFromIndex with individual verses, the new volume will be applied to the NEXT verse in the queue.
    }

    override fun stop() {
        tts?.stop()
    }

    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }
}
