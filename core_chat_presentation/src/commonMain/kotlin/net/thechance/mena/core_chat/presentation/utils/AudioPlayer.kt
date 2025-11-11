package net.thechance.mena.core_chat.presentation.utils

interface AudioPlayer {
    fun play(filePath: String)
    fun pause()
    fun stop()
    fun release()
    fun getDuration(filePath: String): Long
    fun getDurationOfCurrentAudio(): Long
    fun getCurrentPosition(): Long
}

expect fun createAudioPlayer(): AudioPlayer

expect fun convertAudioFileToByteArray(filePath: String): ByteArray