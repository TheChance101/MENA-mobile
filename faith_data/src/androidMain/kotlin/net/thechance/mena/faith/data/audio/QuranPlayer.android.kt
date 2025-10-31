package net.thechance.mena.faith.data.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer
import org.koin.mp.KoinPlatform.getKoin

actual class QuranPlayerImpl() : QuranPlayer {
    private val context = getKoin().get<Context>()
    private val quranPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    actual override fun playAyah(ayahUrl: String) {
        if (ayahUrl.isEmpty()) return

        quranPlayer.stop()
        quranPlayer.clearMediaItems()
        val mediaItem = MediaItem.fromUri(ayahUrl)

        quranPlayer.setMediaItem(mediaItem)
        quranPlayer.prepare()

        quranPlayer.play()
    }

    actual override fun pauseAyah() {
        quranPlayer.pause()
    }


    actual override fun repeatCurrentAyah() {
        val currentAyah = quranPlayer.currentMediaItemIndex
        quranPlayer.seekTo(currentAyah, 0L)
        quranPlayer.play()
    }
}