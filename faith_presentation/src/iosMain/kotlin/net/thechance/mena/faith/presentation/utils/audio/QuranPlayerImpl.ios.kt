package net.thechance.mena.faith.presentation.utils.audio

import kotlinx.cinterop.ExperimentalForeignApi
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.darwin.NSObject

actual class QuranPlayerImpl : QuranPlayer {
    private var player: AVPlayer? = null
    private var completedListener: (() -> Unit)? = null
    private var completionObserver: NSObject? = null

    actual override fun playAyah(ayahUrl: String) {
        if (ayahUrl.isEmpty()) return

        player?.pause()
        completionObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        player = null

        val ayahURL = NSURL.URLWithString(ayahUrl)
        if (ayahURL != null) {
            val item = AVPlayerItem.playerItemWithURL(ayahURL)
            player = AVPlayer(item)
            player?.play()
        }
    }

    actual override fun pauseAyah() {
        player?.pause()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun repeatCurrentAyah() {
        player?.seekToTime(CMTimeMake(0, 1))
        player?.play()
    }

    actual override fun onAyahCompleted(listener: () -> Unit) {
        completedListener = listener
    }
}
