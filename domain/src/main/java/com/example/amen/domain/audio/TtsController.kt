package com.example.amen.domain.audio

interface TtsController {
    fun speak(text: String)
    fun stop()
    fun shutdown()
}
