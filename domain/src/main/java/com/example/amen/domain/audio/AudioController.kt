package com.example.amen.domain.audio

interface AudioController {
    fun playAudio(uriString: String)
    fun setVolume(volume: Float)
    fun startSleepTimerWithFadeOut(minutes: Int)
    fun stop()
    fun release()
}
