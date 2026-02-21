package com.example.amen.domain.audio

interface AudioController {
    fun playAudio(uriString: String)
    /** 배경음(ExoPlayer) 볼륨 즉각 반영 */
    fun setBackgroundVolume(volume: Float)
    /** 슬립 타이머: minutes 분 후 30초에 걸쳐 페이드 아웃 정지 */
    fun startSleepTimerWithFadeOut(minutes: Int)
    fun stop()
    fun release()
    // 하위 호환 유지
    fun setVolume(volume: Float) = setBackgroundVolume(volume)
}
