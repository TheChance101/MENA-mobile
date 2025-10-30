package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle

@Composable
fun SimpleMap(
    initialCameraPosition: CameraPosition
) {
    val cameraState = rememberCameraState(firstPosition = initialCameraPosition)

    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
    )
}
