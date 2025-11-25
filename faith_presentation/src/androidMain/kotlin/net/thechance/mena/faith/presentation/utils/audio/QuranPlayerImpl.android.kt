package net.thechance.mena.faith.presentation.utils.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import org.koin.mp.KoinPlatform.getKoin

actual class QuranPlayerImpl : QuranPlayer {

    private val context = getKoin().get<Context>()
    private val quranPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private var currentUrl: String? = null
    private var completedListener: (() -> Unit)? = null

    init {
        quranPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    completedListener?.invoke()
                }
            }
        })
    }

    actual override fun playAyah(ayahUrl: String) {
        if (ayahUrl.isEmpty()) return

        if (ayahUrl != currentUrl) {
            currentUrl = ayahUrl
            quranPlayer.stop()
            quranPlayer.clearMediaItems()
            quranPlayer.setMediaItem(MediaItem.fromUri(ayahUrl))
            quranPlayer.prepare()
        }

        quranPlayer.play()
    }

    actual override fun pauseAyah() {
        quranPlayer.pause()
    }

    actual override fun repeatCurrentAyah() {
        quranPlayer.seekTo(0)
        quranPlayer.play()
    }

    actual override fun onAyahCompleted(listener: () -> Unit) {
        completedListener = listener
    }
}
