package net.thechance.mena.faith.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal actual class MapNavigatorImpl(
    private val context: Context
) : MapNavigator {
    actual override fun openMapAtCoordinate(coordinate: Coordinate) {
        val lat = coordinate.latitude
        val lon = coordinate.longitude

        val geoIntent = Intent(
            Intent.ACTION_VIEW,
            "geo:$lat,$lon?q=$lat,$lon(Mosque)".toUri()
        )

        if (geoIntent.resolveActivity(context.packageManager) != null) {
            val chooserIntent = Intent.createChooser(geoIntent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
        } else {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                "https://www.google.com/maps/search/?api=1&query=$lat,$lon".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        }
    }
}
