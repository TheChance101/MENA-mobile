@file:OptIn(ExperimentalAtomicApi::class, ExperimentalForeignApi::class)

package net.thechance.mena.core_chat.presentation.utils

import io.github.vinceglb.filekit.utils.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.darwin.NSObject
import kotlin.concurrent.atomics.ExperimentalAtomicApi

actual fun createAudioPlayer(): AudioPlayer {
    return IOSAudioPlayer()
}

actual fun convertAudioFileToByteArray(filePath: String): ByteArray {
    return runCatching {
        NSData.dataWithContentsOfFile(filePath)?.toByteArray() ?: byteArrayOf()
    }.getOrDefault(byteArrayOf())
}

class IOSAudioPlayer : AudioPlayer {
    private var audioPlayer: AVAudioPlayer? = null
    private var currentFilePath: String? = null

    override fun play(filePath: String) {
        if (audioPlayer != null && currentFilePath == filePath && audioPlayer?.isPlaying() == false) {
            audioPlayer?.play()
            return
        }

        stop()

        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(filePath)) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }

        val url = NSURL.fileURLWithPath(filePath)
        val player = AVAudioPlayer(url, null)

        audioPlayer = player
        player.play()

        player.delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
            override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
                player.pause()
            }
        }
        currentFilePath = filePath
    }
    
    override fun pause() {
        if (audioPlayer?.isPlaying() == true) {
            audioPlayer?.pause()
        }
    }

    override fun stop() {
        audioPlayer?.apply {
            if (isPlaying()) stop()
        }
        audioPlayer = null
        currentFilePath = null
    }

    override fun release() {
        stop()
    }

    override fun getDuration(filePath: String): Long {
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(filePath)) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }

        val url = NSURL.fileURLWithPath(filePath)
        val player = AVAudioPlayer(url, null)
        return player.duration.toLong()
    }

    override fun getCurrentPosition(): Long {
        return audioPlayer?.currentTime?.toLong() ?: 0L
    }
}