package net.thechance.mena.faith.presentation.utils.audio

import android.graphics.BitmapFactory
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import net.thechance.mena.faith.presentation.R

@UnstableApi
class MediaDescriptionAdapter(
    private val context: android.content.Context,
    private val pendingIntentProvider: () -> android.app.PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.title ?: "Surah Name"

    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.subtitle ?: "Ayah Number"

    override fun createCurrentContentIntent(player: Player): android.app.PendingIntent? =
        pendingIntentProvider()

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): android.graphics.Bitmap? = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.ic_column_mosque,
    )
}