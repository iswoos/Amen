package com.example.amen.domain.audio

interface TtsController {
    fun speak(text: String)
    /** 여러 구절 중 특정 인덱스부터 낭독 시작 */
    fun speakFromIndex(verses: List<String>, startIndex: Int)
    /** TTS 낭독 볼륨 조절 (0.0f ~ 1.0f) */
    fun setVolume(volume: Float)
    fun stop()
    fun shutdown()
    fun setOnCompletionListener(listener: () -> Unit)
}
