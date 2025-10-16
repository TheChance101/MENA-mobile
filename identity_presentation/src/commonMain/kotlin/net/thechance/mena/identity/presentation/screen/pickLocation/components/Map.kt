package net.thechance.mena.identity.presentation.screen.pickLocation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.Position
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_anchor
import net.thechance.mena.identity.presentation.screen.pickLocation.PickLocationScreenUIState
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@Composable
fun Map(
    anchorLocation: DpOffset?,
    isLocked: Boolean,
    cameraPosition: CameraPosition,
    currentLocation: PickLocationScreenUIState.CoordinatesUiState?,
    onMapClick: (PickLocationScreenUIState.CoordinatesUiState, DpOffset) -> Unit,
    onCameraMoved: (CameraPosition) -> Unit,
    animateToCurrentLocation: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val camera = rememberCameraState(firstPosition = cameraPosition)

    LaunchedEffect(Unit) {
        camera.animateTo(finalPosition = cameraPosition)
    }

    LaunchedEffect(camera) {
        snapshotFlow { camera.position }.collect { position -> onCameraMoved(position) }
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        SetAnchorInCenterScreenWhenUseGps(
            animateToCurrentLocation = animateToCurrentLocation,
            currentLocation = currentLocation,
            onMapClick = onMapClick,
            camera = camera,
            maxWidth = maxWidth,
            maxHeight = maxHeight
        )

        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(BRIGHT),
            onMapClick = { position, offset ->
                if (isLocked) {
                    ClickResult.Consume
                } else {
                    onMapClick(
                        PickLocationScreenUIState.CoordinatesUiState(
                            latitude = position.latitude,
                            longitude = position.longitude
                        ),
                        offset,
                    )
                    ClickResult.Pass
                }
            },
            options = mapOptions(isLocked)
        )

        Anchor(anchorLocation = anchorLocation)
        content()
    }
}

@Composable
private fun SetAnchorInCenterScreenWhenUseGps(
    animateToCurrentLocation: Boolean,
    currentLocation: PickLocationScreenUIState.CoordinatesUiState?,
    onMapClick: (PickLocationScreenUIState.CoordinatesUiState, DpOffset) -> Unit,
    camera: CameraState,
    maxWidth: Dp,
    maxHeight: Dp
){
    LaunchedEffect(animateToCurrentLocation) {
        if (animateToCurrentLocation && currentLocation != null) {
            camera.animateTo(
                finalPosition = CameraPosition(
                    target = Position(
                        longitude = currentLocation.longitude,
                        latitude = currentLocation.latitude
                    ),
                    zoom = 16.0
                )
            )

            onMapClick(
                PickLocationScreenUIState.CoordinatesUiState(
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude
                ),
                DpOffset(
                    x = maxWidth / 2f,
                    y = maxHeight / 2f
                ),
            )
        }
    }
}

@Composable
private fun Anchor(
    anchorLocation: DpOffset?,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = anchorLocation
    ) {
        it?.let { offset ->
            Image(
                painter = painterResource(Res.drawable.ic_anchor),
                contentDescription = null,
                modifier = modifier
                    .size(46.dp, 58.05.dp)
                    .offset(
                        x = offset.x - 23.dp,
                        y = offset.y - 50.05.dp
                    )
            )
        }
    }
}

private fun mapOptions(
    isLocked: Boolean
): MapOptions {
    return MapOptions(
        gestureOptions = if (isLocked) {
            GestureOptions.AllDisabled
        } else {
            GestureOptions.Standard
        },
        ornamentOptions = OrnamentOptions.AllDisabled,
        renderOptions = RenderOptions.Standard
    )
}

private const val BRIGHT = "https://tiles.openfreemap.org/styles/bright"