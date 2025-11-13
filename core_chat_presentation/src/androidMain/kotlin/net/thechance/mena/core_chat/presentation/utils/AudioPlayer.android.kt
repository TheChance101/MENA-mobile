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

    override fun play(filePath: String) {
        if (mediaPlayer != null && currentFilePath == filePath && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            return
        }

        stop()

        if (!File(filePath).exists()) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepareAsync()
            setOnPreparedListener { start() }
            setOnErrorListener { _, what, extra ->
                throw IllegalStateException("MediaPlayer error: what=$what, extra=$extra")
            }
            setOnCompletionListener { stop() }
        }
        currentFilePath = filePath
    }
    
    override fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                reset()
                release()
            }
        } finally {
            mediaPlayer = null
            currentFilePath = null
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
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }
}