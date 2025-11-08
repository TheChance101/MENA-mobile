@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.audio

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.defaultAudioDownloadResponse
import net.thechance.mena.core_chat.data.repository.AudioRecordRepositoryImpl
import net.thechance.mena.core_chat.data.utils.FileManager
import net.thechance.mena.core_chat.data.utils.audio.AudioRecorder
import net.thechance.mena.core_chat.domain.exception.AudioFileNotFound
import net.thechance.mena.core_chat.domain.exception.InvalidAudioFile
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import net.thechance.mena.core_chat.domain.exception.SaveAudioFailed
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi

class AudioRecordRepositoryImplTest {

    private lateinit var httpClient: HttpClient
    private lateinit var fileManager: FileManager
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var repository: AudioRecordRepositoryImpl

    @BeforeTest
    fun setUp() {
        httpClient = createHttpClient()
        fileManager = mock<FileManager>()
        audioRecorder = mock<AudioRecorder>()

        every { fileManager.getCacheDirectory("audio_messages") } returns "/cache/audio_messages"

        repository = AudioRecordRepositoryImpl(
            client = httpClient,
            fileManager = fileManager,
            audioRecorder = audioRecorder
        )
    }

    @Test
    fun `should start recording when startRecording is called`() {
        // Given
        every { audioRecorder.startRecording() } returns Unit
        every { audioRecorder.isRecording() } returns true

        // When
        repository.startRecording()

        // Then
        val isRecording = repository.isRecording()
        assertThat(isRecording).isTrue()
    }

    @Test
    fun `should return true when isRecording is called after starting recording`() {
        // Given
        every { audioRecorder.startRecording() } returns Unit
        every { audioRecorder.isRecording() } returns true
        repository.startRecording()

        // When
        val result = repository.isRecording()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should stop recording and return file path when stopRecording is called`() {
        // Given
        every { audioRecorder.startRecording() } returns Unit
        every { audioRecorder.stopRecording() } returns "/cache/audio/recording.wav"
        every { audioRecorder.isRecording() } sequentially {
            returns(true)  // After start
            returns(false) // After stop
        }

        repository.startRecording()

        // consume the first isRecording() sequential value (true)
        assertThat(repository.isRecording()).isTrue()

        // When
        val filePath = repository.stopRecording()

        // Then
        assertThat(filePath).isNotEmpty()
        assertThat(repository.isRecording()).isFalse()
    }

    @Test
    fun `should return valid file path after stopping recording`() {
        // Given
        every { audioRecorder.startRecording() } returns Unit
        every { audioRecorder.stopRecording() } returns "/cache/audio/recording.wav"

        repository.startRecording()

        // When
        val filePath = repository.stopRecording()

        // Then
        assertThat(filePath).isNotEmpty()
        assertThat(filePath).contains(".wav")
    }

    @Test
    fun `should return false when isRecording is called before starting recording`() {
        // Given
        every { audioRecorder.isRecording() } returns false

        // When
        val result = repository.isRecording()

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false after stopping recording`() {
        // Given
        every { audioRecorder.startRecording() } returns Unit
        every { audioRecorder.stopRecording() } returns "/cache/audio/recording.wav"
        every { audioRecorder.isRecording() } returns false

        repository.startRecording()
        repository.stopRecording()

        // When
        val result = repository.isRecording()

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should toggle recording state correctly`() {
        // Given
        every { audioRecorder.startRecording() } returns Unit
        every { audioRecorder.stopRecording() } returns "/cache/audio/recording.wav"
        every { audioRecorder.isRecording() } sequentially {
            returns(false)  // Initially not recording
            returns(true)   // After first start
            returns(false)  // After stop
            returns(true)   // After second start
        }

        // Initially not recording
        assertThat(repository.isRecording()).isFalse()

        // Start recording
        repository.startRecording()
        assertThat(repository.isRecording()).isTrue()

        // Stop recording
        repository.stopRecording()
        assertThat(repository.isRecording()).isFalse()

        // Start again
        repository.startRecording()
        assertThat(repository.isRecording()).isTrue()
    }

    @Test
    fun `should return cached file path when audio file exists locally`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio.m4a"
        val expectedPath =
            "/cache/audio_messages/audio_${audioUrl.hashCode().toString().replace("-", "")}.m4a"

        every { fileManager.isFileExists(expectedPath) } returns true
        every { fileManager.getFileSize(expectedPath) } returns 1024L

        // When
        val result = repository.getAudioFilePath(audioUrl)

        // Then
        assertThat(result).isEqualTo(expectedPath)
        verifySuspend { fileManager.isFileExists(expectedPath) }
    }

    @Test
    fun `should download and cache audio when file does not exist locally`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio.m4a"
        val expectedPath =
            "/cache/audio_messages/audio_${audioUrl.hashCode().toString().replace("-", "")}.m4a"
        val audioBytes = ByteArray(1024) { it.toByte() }

        httpClient = createHttpClient(
            audioDownloadResponse = { defaultAudioDownloadResponse(audioBytes) }
        )

        every { fileManager.isFileExists(expectedPath) } sequentially {
            returns(false)
            returns(true)
        }
        everySuspend { fileManager.writeFile(any(), any()) } returns true
        every { fileManager.getFileSize(expectedPath) } returns 1024L

        repository = AudioRecordRepositoryImpl(
            client = httpClient,
            fileManager = fileManager,
            audioRecorder = audioRecorder
        )

        // When
        val result = repository.getAudioFilePath(audioUrl)

        // Then
        assertThat(result).isEqualTo(expectedPath)
        verifySuspend { fileManager.writeFile(any(), any()) }
    }

    @Test
    fun `should throw NotFoundException when audio file not found on server`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio.m4a"

        httpClient = createHttpClient(
            audioDownloadResponse = { respondError(HttpStatusCode.NotFound) }
        )

        repository = AudioRecordRepositoryImpl(
            client = httpClient,
            fileManager = fileManager,
            audioRecorder = audioRecorder
        )

        every { fileManager.isFileExists(any()) } returns false

        // When & Then
        val exception = assertFailsWith<NotFoundException> {
            repository.getAudioFilePath(audioUrl)
        }

        assertThat((exception.message ?: "").lowercase()).contains("not found")
    }

    @Test
    fun `should throw SaveAudioFailed when file write operation fails`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio.m4a"
        val audioBytes = ByteArray(1024) { it.toByte() }

        httpClient = createHttpClient(
            audioDownloadResponse = { defaultAudioDownloadResponse(audioBytes) }
        )

        every { fileManager.isFileExists(any()) } returns false
        everySuspend { fileManager.writeFile(any(), any()) } returns false

        repository = AudioRecordRepositoryImpl(
            client = httpClient,
            fileManager = fileManager,
            audioRecorder = audioRecorder
        )

        // When & Then
        val exception = assertFailsWith<SaveAudioFailed> {
            repository.getAudioFilePath(audioUrl)
        }

        assertThat(exception.message ?: "").contains("Failed to save audio")
    }

    @Test
    fun `should throw AudioFileNotFound when downloaded file does not exist after write`() =
        runTest {
            // Given
            val audioUrl = "http://example.com/audio.m4a"
            val audioBytes = ByteArray(1024) { it.toByte() }

            httpClient = createHttpClient(
                audioDownloadResponse = { defaultAudioDownloadResponse(audioBytes) }
            )

            every { fileManager.isFileExists(any()) } sequentially {
                returns(false)
                returns(false)
            }
            everySuspend { fileManager.writeFile(any(), any()) } returns true

            repository = AudioRecordRepositoryImpl(
                client = httpClient,
                fileManager = fileManager,
                audioRecorder = audioRecorder
            )

            // When & Then
            val exception = assertFailsWith<AudioFileNotFound> {
                repository.getAudioFilePath(audioUrl)
            }

            assertThat(exception.message ?: "").contains("not found")
        }

    @Test
    fun `should throw InvalidAudioFile when downloaded file size is zero`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio.m4a"
        val expectedPath =
            "/cache/audio_messages/audio_${audioUrl.hashCode().toString().replace("-", "")}.m4a"

        every { fileManager.isFileExists(expectedPath) } returns true
        every { fileManager.getFileSize(expectedPath) } returns 0L

        // When & Then
        val exception = assertFailsWith<InvalidAudioFile> {
            repository.getAudioFilePath(audioUrl)
        }

        assertThat(exception.message ?: "").contains("empty")
    }

    @Test
    fun `should throw exception when network error occurs during download`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio.m4a"

        httpClient = createHttpClient(
            audioDownloadResponse = { respondError(HttpStatusCode.InternalServerError) }
        )

        repository = AudioRecordRepositoryImpl(
            client = httpClient,
            fileManager = fileManager,
            audioRecorder = audioRecorder
        )

        every { fileManager.isFileExists(any()) } returns false

        // When & Then
        assertFailsWith<Exception> {
            repository.getAudioFilePath(audioUrl)
        }
    }

    @Test
    fun `should handle URL with special characters correctly`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio-file_2025-11-04.m4a?token=abc123"
        val expectedPath =
            "/cache/audio_messages/audio_${audioUrl.hashCode().toString().replace("-", "")}.m4a"
        val audioBytes = ByteArray(512) { it.toByte() }

        httpClient = createHttpClient(
            audioDownloadResponse = { defaultAudioDownloadResponse(audioBytes) }
        )

        every { fileManager.isFileExists(expectedPath) } sequentially {
            returns(false)
            returns(true)
        }
        everySuspend { fileManager.writeFile(any(), any()) } returns true
        every { fileManager.getFileSize(expectedPath) } returns 512L

        repository = AudioRecordRepositoryImpl(
            client = httpClient,
            fileManager = fileManager,
            audioRecorder = audioRecorder
        )


        // When
        val result = repository.getAudioFilePath(audioUrl)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result).contains("audio_")
    }

    @Test
    fun `should handle concurrent requests for same audio URL`() = runTest {
        // Given
        val audioUrl = "http://example.com/audio.m4a"
        val expectedPath =
            "/cache/audio_messages/audio_${audioUrl.hashCode().toString().replace("-", "")}.m4a"

        every { fileManager.isFileExists(expectedPath) } returns true
        every { fileManager.getFileSize(expectedPath) } returns 2048L

        // When
        val result1 = repository.getAudioFilePath(audioUrl)
        val result2 = repository.getAudioFilePath(audioUrl)

        // Then
        assertThat(result1).isEqualTo(result2)
        assertThat(result1).isEqualTo(expectedPath)
    }

    @Test
    fun `should record audio and get valid file path`() {
        // Given
        every { audioRecorder.startRecording() } returns Unit
        every { audioRecorder.stopRecording() } returns "/cache/audio/recording.wav"
        every { audioRecorder.isRecording() } sequentially {
            returns(true)  // After start
            returns(false) // After stop
        }

        // Given - Start recording
        repository.startRecording()
        // consume first sequential true
        assertThat(repository.isRecording()).isTrue()

        // When - Stop recording
        val filePath = repository.stopRecording()

        // Then
        assertThat(filePath).isNotEmpty()
        assertThat(repository.isRecording()).isFalse()
    }

    @Test
    fun `should not be recording initially`() {
        // Given
        every { audioRecorder.isRecording() } returns false

        // When
        val isRecording = repository.isRecording()

        // Then
        assertThat(isRecording).isFalse()
    }
}