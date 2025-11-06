package net.thechance.mena.core_chat.data.utils.audio

interface AudioRecorder {
    fun startRecording()
    fun stopRecording(): String
    fun isRecording(): Boolean
}