package net.thechance.mena.core_chat.presentation.utils

import android.media.MediaPlayer
import java.io.File

actual fun createAudioPlayer(): AudioPlayer {
    return AndroidAudioPlayer()
}

actual fun convertAudioFileToByteArray(filePath: String): ByteArray {
    return runCatching { File(filePath).readBytes() }.getOrDefault(byteArrayOf())
}

class AndroidAudioPlayer : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var currentFilePath: String? = null
    private var isPrepared = false

    override fun play(filePath: String) {
        if (mediaPlayer != null && currentFilePath == filePath && isPrepared) {
            try {
                val mp = mediaPlayer
                if (mp != null && !mp.isPlaying) {
                    mp.start()
                    return
                }
            } catch (e: IllegalStateException) {
                releaseMediaPlayer()
            }
        }

        releaseMediaPlayer()

        if (!File(filePath).exists()) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }

        isPrepared = false
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            setOnPreparedListener {
                isPrepared = true
                start()
            }
            setOnErrorListener { _, what, extra ->
                isPrepared = false
                throw IllegalStateException("MediaPlayer error: what=$what, extra=$extra")
            }
            setOnCompletionListener {
                isPrepared = false
            }
            prepareAsync()
        }
        currentFilePath = filePath
    }

    override fun pause() {
        runCatching { mediaPlayer?.takeIf { it.isPlaying }?.pause() }
    }

    override fun stop() {
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
        } catch (e: IllegalStateException) {
            runCatching {
                mediaPlayer?.release()
            }
        } finally {
            mediaPlayer = null
            currentFilePath = null
            isPrepared = false
        }
    }

    override fun release() {
        stop()
    }

    override fun getDuration(filePath: String): Long {
        if (!File(filePath).exists()) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }

        var tempPlayer: MediaPlayer? = null
        return try {
            tempPlayer = MediaPlayer()
            tempPlayer.setDataSource(filePath)
            tempPlayer.prepare()
            tempPlayer.duration.toLong()
        } finally {
            tempPlayer?.release()
        }
    }

    override fun getDurationOfCurrentAudio(): Long {
        return currentFilePath?.let(::getDuration) ?: throw IllegalArgumentException("there is not current audio")
    }
    
    override fun getCurrentPosition(): Long {
        return try {
            mediaPlayer?.currentPosition?.toLong() ?: 0L
        } catch (e: IllegalStateException) {
            0L
        }
    }
}