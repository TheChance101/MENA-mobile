@file:OptIn(ExperimentalAtomicApi::class, ExperimentalForeignApi::class)

package net.thechance.mena.core_chat.presentation.utils

import io.github.vinceglb.filekit.utils.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFoundation.AVURLAsset
import platform.CoreMedia.CMTimeGetSeconds
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
    private var cachedDurationMs: Long = 0L

    override fun play(filePath: String) {
        if (audioPlayer != null && currentFilePath == filePath) {
            if (audioPlayer?.isPlaying() == false) audioPlayer?.play()
            return
        }
        stop()
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(filePath)) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }
        val url = NSURL.fileURLWithPath(filePath)
        val player = AVAudioPlayer(url, null)
        player.prepareToPlay()
        audioPlayer = player
        cachedDurationMs = (player.duration * 1000).toLong().coerceAtLeast(0L)
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
        audioPlayer?.apply { if (isPlaying()) stop() }
        audioPlayer = null
        currentFilePath = null
        cachedDurationMs = 0L
    }

    override fun release() { stop() }

    override fun getDuration(filePath: String): Long {
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(filePath)) {
            throw IllegalArgumentException("File does not exist: $filePath")
        }
        val url = NSURL.fileURLWithPath(filePath)
        val asset = AVURLAsset.URLAssetWithURL(url, null)
        val seconds = CMTimeGetSeconds(asset.duration)
        return if (seconds.isFinite() && seconds > 0) (seconds * 1000).toLong() else 0L
    }

    override fun getDurationOfCurrentAudio(): Long {
        val path = currentFilePath ?: throw IllegalArgumentException("There is no current audio loaded")
        return if (cachedDurationMs > 0) cachedDurationMs else getDuration(path)
    }

    override fun getCurrentPosition(): Long {
        return ((audioPlayer?.currentTime ?: 0.0) * 1000).toLong()
    }
}