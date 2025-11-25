package net.thechance.mena.faith.presentation.utils.audio

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.NotificationUtil.IMPORTANCE_LOW
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import net.thechance.mena.faith.presentation.R

/**
 * A foreground service for playing Quran audio using ExoPlayer.
 *
 * This service manages the player's lifecycle, handles audio focus, and displays
 * media playback notifications. It is designed to be controlled by a bound client.
 */
@UnstableApi
class QuranPlayerService : Service() {

    private val serviceBinder: IBinder = ServiceBinder()

    val player: ExoPlayer by lazy { createPlayer() }

    private val notificationManager: PlayerNotificationManager by lazy { createNotificationManager() }

    inner class ServiceBinder : Binder() {
        fun getPlayerService(): QuranPlayerService = this@QuranPlayerService
    }

    override fun onBind(intent: Intent): IBinder = serviceBinder

    override fun onCreate() {
        super.onCreate()
        notificationManager.setPlayer(player)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.run {
            if (isPlaying) stop()
            release()
        }
        notificationManager.setPlayer(null)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createPlayer(): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        return ExoPlayer.Builder(applicationContext).build().also { exoPlayer ->
            exoPlayer.setAudioAttributes(audioAttributes, true)
        }
    }

    private fun createNotificationManager(): PlayerNotificationManager {
        val mediaDescriptionAdapter = MediaDescriptionAdapter(
            context = this,
            pendingIntentProvider = {
                packageManager?.getLaunchIntentForPackage(packageName)?.let { intent ->
                    val flags =
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                    android.app.PendingIntent.getActivity(this, 0, intent, flags)
                }
            }
        )

        return PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            CHANNEL_ID
        ).setChannelNameResourceId(android.R.string.untitled)
            .setChannelDescriptionResourceId(android.R.string.untitled)
            .setChannelImportance(IMPORTANCE_LOW)
            .setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setNotificationListener(createNotificationListener())
            .setSmallIconResourceId(R.drawable.ic_quran)
            .setPlayActionIconResourceId(R.drawable.icon_play)
            .setPauseActionIconResourceId(R.drawable.icon_pause)
            .setNextActionIconResourceId(R.drawable.icon_next)
            .setPreviousActionIconResourceId(R.drawable.icon_previous)
            .build()
            .apply {
                setUseFastForwardAction(false)
                setUseRewindAction(false)
            }
    }

    private fun createNotificationListener() =
        object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                if (dismissedByUser) {
                    stopSelf()
                }
            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean,
            ) {
                if (ongoing) {
                    startForeground(notificationId, notification)
                } else {
                    stopForeground(STOP_FOREGROUND_DETACH)
                }
            }
        }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "quran_audio_playback"
    }
}
