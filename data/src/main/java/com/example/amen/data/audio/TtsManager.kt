package com.example.amen.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
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

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 언어 지원 안됨 핸들링
                isInitialized = false
            } else {
                // 요구사항에 따른 경건하고 편안한 톤 세팅
                tts?.setPitch(0.8f)      // 약간 낮은 톤으로 안정감 스팅
                tts?.setSpeechRate(0.85f)// 천천히 낭독
                isInitialized = true
            }
        }
    }

    override fun speak(text: String) {
        if (!isInitialized) return
        
        // 문장/구절 간 여백(무음)을 주기 위해 온점을 기준으로 텍스트에 물리적인 딜레이 요소를 넣거나
        // playSilentUtterance를 체이닝할 수 있습니다. 가장 간단한 방법은 구두점을 살리는 것입니다.
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID_${System.currentTimeMillis()}")
    }

    override fun stop() {
        tts?.stop()
    }

    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
