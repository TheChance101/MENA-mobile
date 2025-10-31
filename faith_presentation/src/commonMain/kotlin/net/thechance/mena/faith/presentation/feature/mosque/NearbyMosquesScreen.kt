package net.thechance.mena.faith.presentation.feature.mosque

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle

@Composable
internal fun NearbyMosquesScreen(
    viewModel: NearbyMosquesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val initialCameraPosition = CameraPosition(
        target = Position(
            longitude = state.centerOfMap?.longitude ?: 0.0,
            latitude = state.centerOfMap?.latitude ?: 0.0
        ),
        zoom = 14.0
    )
    val cameraState = rememberCameraState(firstPosition = initialCameraPosition)

    LaunchedEffect(cameraState) {
        snapshotFlow { cameraState.position }
            .collect {
                viewModel.mapPositionChanged(
                    latitude = cameraState.position.target.latitude,
                    longitude = cameraState.position.target.longitude
                )
            }

    }

    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
    )
}
