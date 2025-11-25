package net.thechance.mena.faith.presentation.utils.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer

actual class QuranPlayerImpl(
    private val context: Context,
) : QuranPlayer {


    private var quranPlayer: ExoPlayer? = null

    private var currentUrl: String? = null
    private var completedListener: (() -> Unit)? = null

    private val playerServiceConnection by lazy { PlayerServiceConnection() }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_ENDED) {
                completedListener?.invoke()
            }
        }
    }

    init {
        bindToService()
    }

    inner class PlayerServiceConnection : ServiceConnection {
        @OptIn(UnstableApi::class)
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder: QuranPlayerService.ServiceBinder =
                service as QuranPlayerService.ServiceBinder
            quranPlayer = binder.getPlayerService().player
            quranPlayer?.addListener(playerListener)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            quranPlayer = null
        }
    }

    @OptIn(UnstableApi::class)
    private fun bindToService() {
        val intent = Intent(context, QuranPlayerService::class.java)
        context.bindService(intent, playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    actual override fun playAyah(ayahUrl: String) {
        if (ayahUrl.isEmpty()) return
        quranPlayer?.let { player ->
            val isNewAyah = ayahUrl != currentUrl
            if (isNewAyah) {
                currentUrl = ayahUrl
                player.setMediaItem(MediaItem.fromUri(ayahUrl), true)
                player.prepare()
            }
            player.play()
        }
    }

    @OptIn(UnstableApi::class)
    actual override fun playAyah(
        ayahUrl: String,
        surahName: String,
        ayahNumber: Int,
        reciterName: String,
    ) {
        if (ayahUrl.isEmpty()) return
        quranPlayer?.let { player ->
            val isNewAyah = ayahUrl != currentUrl
            if (isNewAyah) {
                currentUrl = ayahUrl
                val mediaItem = MediaItem.Builder()
                    .setUri(ayahUrl)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(surahName)
                            .setSubtitle("$reciterName: $ayahNumber")
                            .build()
                    ).build()
                player.setMediaItem(mediaItem, true)
                player.prepare()
            }
            player.play()
            context.startService(Intent(context.applicationContext, QuranPlayerService::class.java))
        }
    }

    actual override fun pauseAyah() {
        quranPlayer?.pause()
    }

    actual override fun repeatCurrentAyah() {
        quranPlayer?.apply {
            seekTo(0)
            play()
        }
    }

    actual override fun onAyahCompleted(listener: () -> Unit) {
        completedListener = listener
    }
}
