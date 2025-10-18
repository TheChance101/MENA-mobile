package net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.launch
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_anchor
import net.thechance.mena.identity.presentation.components.util.MapStyle
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.AddressModel
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenUIState
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
fun PickLocationMap(
    anchorLocation: DpOffset?,
    isLocked: Boolean,
    cameraPosition: CameraPosition,
    currentLocation: PickLocationScreenUIState.CoordinatesUiState?,
    onMapClick: (PickLocationScreenUIState.CoordinatesUiState, DpOffset) -> Unit,
    onCameraMoved: (CameraPosition) -> Unit,
    onSetAnchorLocation: (DpOffset) -> Unit,
    onUpdateAddress: (addressModel: AddressModel?) -> Unit,
    addressModel: AddressModel?,
    modifier: Modifier = Modifier,
    animateToCurrentLocation: Boolean = false,
    content: @Composable () -> Unit,
) {

    LaunchedEffect(addressModel) {
        onUpdateAddress(addressModel)
    }

    val camera = rememberCameraState(firstPosition = cameraPosition)
    LaunchedEffect(Unit) { camera.animateTo(finalPosition = cameraPosition) }

    LaunchedEffect(camera) {
        snapshotFlow { camera.position }.collect { position -> onCameraMoved(position) }
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        SetAnchorInCenter(
            animateToCurrentLocation = animateToCurrentLocation,
            longitude = currentLocation?.longitude,
            latitude = currentLocation?.latitude,
            onSetAnchorLocation = onSetAnchorLocation,
            camera = camera,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
        )

        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { position, offset ->
                onMapClick(
                    PickLocationScreenUIState.CoordinatesUiState(
                        latitude = position.latitude,
                        longitude = position.longitude
                    ),
                    offset,
                )
                ClickResult.Pass
            },
            options = mapOptions(isLocked)
        )

        Anchor(anchorLocation = anchorLocation)
        content()
    }
}

@Composable
fun SetAnchorInCenter(
    animateToCurrentLocation: Boolean,
    longitude: Double?,
    latitude: Double?,
    onSetAnchorLocation: (DpOffset) -> Unit,
    camera: CameraState,
    maxWidth: Dp,
    maxHeight: Dp,
) {
    val coroutineScope = rememberCoroutineScope()
    if (animateToCurrentLocation && longitude != null && latitude != null) {
        coroutineScope.launch {
            camera.animateTo(
                finalPosition = camera.position.copy(
                    target = Position(
                        longitude = longitude,
                        latitude = latitude
                    ),
                    zoom = 16.0
                )
            )
            onSetAnchorLocation(
                DpOffset(
                    x = maxWidth / 2f,
                    y = maxHeight / 2f
                ),
            )
        }
    }
}

@Composable
fun Anchor(
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

