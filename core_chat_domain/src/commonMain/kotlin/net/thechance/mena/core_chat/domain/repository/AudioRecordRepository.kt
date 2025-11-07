package net.thechance.mena.core_chat.domain.repository

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
interface AudioRecordRepository {
    fun startRecording()
    fun stopRecording(): String
    fun isRecording(): Boolean
    suspend fun getAudioFilePath(url: String): String
}