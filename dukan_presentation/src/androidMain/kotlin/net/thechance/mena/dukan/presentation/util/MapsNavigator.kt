package net.thechance.mena.dukan.presentation.util

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.core.net.toUri

actual object MapsNavigator{
    actual fun getDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        context: Any?
    ) {
        val ctx = context as? android.content.Context ?: return

        val url = "https://www.google.com/maps/dir/?api=1" +
                "&origin=$startLat,$startLng" +
                "&destination=$endLat,$endLng" +
                "&travelmode=driving"

        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        ctx.startActivity(intent)
    }

}