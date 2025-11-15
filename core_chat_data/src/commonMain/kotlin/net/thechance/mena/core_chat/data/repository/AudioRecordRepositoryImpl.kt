package net.thechance.mena.core_chat.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import net.thechance.mena.core_chat.data.utils.FileManager
import net.thechance.mena.core_chat.data.utils.audio.AudioRecorder
import net.thechance.mena.core_chat.data.utils.tryNetworkCall
import net.thechance.mena.core_chat.domain.exception.AudioFileNotFound
import net.thechance.mena.core_chat.domain.exception.InvalidAudioFile
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.SaveAudioFailed
import net.thechance.mena.core_chat.domain.repository.AudioRecordRepository
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AudioRecordRepositoryImpl(
    private val client: HttpClient,
    private val fileManager: FileManager,
    private val audioRecorder: AudioRecorder,
) : AudioRecordRepository {

    override fun startRecording() {
        audioRecorder.startRecording()
    }

    override fun stopRecording(): String {
        return audioRecorder.stopRecording()
    }

    override fun isRecording(): Boolean {
        return audioRecorder.isRecording()
    }

    override suspend fun getAudioFilePath(url: String): String {
        return withContext(Dispatchers.IO) {
            val localPath = downloadAudioFile(url)
            validateAudioFile(localPath)
            localPath
        }
    }

    private suspend fun downloadAudioFile(audioUrl: String): String {
        val cacheDir = fileManager.getCacheDirectory("audio_messages")
        val fileName = "audio_${audioUrl.hashCode().toString().replace("-", "")}.m4a"
        val cachedFilePath = "$cacheDir/$fileName"

        if (fileManager.isFileExists(cachedFilePath)) {
            return cachedFilePath
        }

        val audioBytes = tryNetworkCall<ByteArray>(
            bodyType = typeInfo<ByteArray>(),
            maxAttempts = 3
        ) {
            client.get(audioUrl)
        } ?: throw NotFoundException("Audio file not found on server")

        val success = fileManager.writeFile(cachedFilePath, audioBytes)
        if (!success) {
            throw SaveAudioFailed("Failed to save audio")
        }

        return cachedFilePath
    }

    private fun validateAudioFile(filePath: String) {
        if (!fileManager.isFileExists(filePath)) {
            throw AudioFileNotFound("Audio file not found: $filePath")
        }

        if (fileManager.getFileSize(filePath) == 0L) {
            throw InvalidAudioFile("Audio file is empty: $filePath")
        }
    }
}