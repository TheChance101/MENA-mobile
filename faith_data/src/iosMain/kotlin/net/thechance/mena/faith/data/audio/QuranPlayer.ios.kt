package net.thechance.mena.faith.data.audio

import kotlinx.cinterop.ExperimentalForeignApi
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import platform.AVFoundation.*
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.darwin.NSObject

actual class QuranPlayerImpl : QuranPlayer {

    private var player: AVPlayer? = null
    private val mediaSession = MediaSessionController()  // 🔥 new
    private var completedListener: (() -> Unit)? = null
    private var completionObserver: NSObject? = null

    actual override fun playAyah(
        ayahUrl: String,
        surahName: String,
        ayahNumber: Int,
        reciterName: String,
    ) {
        if (ayahUrl.isEmpty()) return

        player?.pause()
        completionObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        player = null

        val url = NSURL.URLWithString(ayahUrl)
        if (url != null) {
            val item = AVPlayerItem.playerItemWithURL(url)
            player = AVPlayer(item)

            setupRemoteCommands(player!!)
            updateMetadata(surahName, reciterName, ayahNumber)

            player?.play()
        }
    }

    private fun setupRemoteCommands(avPlayer: AVPlayer) {
        mediaSession.bindPlayer(avPlayer)
    }

    private fun updateMetadata(
        surah: String,
        reciter: String,
        ayahNumber: Int,
    ) {
        mediaSession.updateNowPlaying(
            title = "$surah - Ayah $ayahNumber",
            artist = reciter,
            artworkName = "quran_artwork",
        )
    }

    actual override fun pauseAyah() {
        player?.pause()
        mediaSession.updatePlaybackState(isPlaying = false)
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
