package net.thechance.mena.faith.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import net.thechance.mena.faith.presentation.feature.mosque.Coordinate

internal actual class MapNavigatorImpl(
    private val context: Context
) : MapNavigator {
    actual override fun openMapAtCoordinate(coordinate: Coordinate)  {
        val lat = coordinate.latitude
        val lon = coordinate.longitude

        val gmmIntentUri = "geo:$lat,$lon?q=$lat,$lon(Mosque)".toUri()
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                "https://www.google.com/maps/search/?api=1&query=$lat,$lon".toUri()
            )
            context.startActivity(browserIntent)
        }
    }
}
