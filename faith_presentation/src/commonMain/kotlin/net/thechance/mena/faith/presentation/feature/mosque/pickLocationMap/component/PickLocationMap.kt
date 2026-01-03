package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.mosque_pin
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.CoordinatesUiState
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.toCoordinatesUiState
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.toPosition
import net.thechance.mena.faith.presentation.utils.MapStyle
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@Composable
internal fun PickLocationMap(
    currentLocation: CoordinatesUiState,
    animateToCurrentLocation: Boolean,
    showAnchor: Boolean,
    onMapClick: (CoordinatesUiState) -> Unit,
    onMoveCamera: (CoordinatesUiState) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val camera = rememberCameraState(
        firstPosition = CameraPosition(
            target = currentLocation.toPosition(), zoom = 1.5
        )
    )

    LaunchedEffect(camera) {
        snapshotFlow { camera.isCameraMoving }.distinctUntilChanged()
            .filter { isMoving -> !isMoving }.drop(2)
            .collect { onMoveCamera(camera.position.target.toCoordinatesUiState()) }
    }

    LaunchedEffect(camera, currentLocation) {
        snapshotFlow { currentLocation }.collect { location ->
            val zoom = if (animateToCurrentLocation) 16.0 else camera.position.zoom
            camera.animateTo(
                finalPosition = camera.position.copy(
                    target = location.toPosition(), zoom = zoom
                )
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            options = mapOptions(),
            onMapClick = { position, _ ->
                onMapClick(position.toCoordinatesUiState())
                ClickResult.Pass
            },
        )

        MapAnchor(showAnchor)

        content()
    }
}

@Composable
private fun BoxScope.MapAnchor(showAnchor: Boolean) {
    AnimatedVisibility(
        visible = showAnchor,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.Center)
    ) {
        Image(
            painter = painterResource(Res.drawable.mosque_pin),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.padding(bottom = 58.dp).height(58.dp)
        )
    }
}

private fun mapOptions(): MapOptions {
    return MapOptions(
        gestureOptions = GestureOptions.Standard,
        ornamentOptions = OrnamentOptions.AllDisabled,
        renderOptions = RenderOptions.Standard
    )
}
