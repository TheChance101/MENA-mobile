package net.thechance.mena.faith.presentation.feature.mosque

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle

@Composable
fun NearbyMosquesScreen() {
    val initialCameraPosition = CameraPosition(
        target = Position(longitude = 14.7749, latitude = -5.4194),
        zoom = 14.0
    )
    val cameraState = rememberCameraState(firstPosition = initialCameraPosition)

    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
    )
}
