package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import net.thechance.mena.identity.presentation.components.util.MapStyle
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.Anchor
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.EditMapButton
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.SetAnchorInCenter
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddLocationMap(
    cameraPosition: CameraPosition,
    longitude: Double?,
    latitude: Double?,
    onEditClick: () -> Unit,
    onClickMap: () -> Unit,
    onSetAnchorLocation: (DpOffset) -> Unit,
    modifier: Modifier = Modifier,
    anchorLocation: DpOffset? = null,
    animateToCurrentLocation: Boolean = false
) {

    val camera = rememberCameraState(firstPosition = cameraPosition)

    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition = cameraPosition,
        )
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        SetAnchorInCenter(
            animateToCurrentLocation = animateToCurrentLocation,
            longitude = longitude,
            latitude = latitude,
            onSetAnchorLocation = onSetAnchorLocation,
            camera = camera,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
        )

            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = camera,
                baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
                onMapClick = { _, _ ->
                    if (longitude == 29.0) {
                        onClickMap()
                    }
                    ClickResult.Consume
                },
                options =
                    MapOptions(
                        gestureOptions = GestureOptions.AllDisabled,
                        ornamentOptions = OrnamentOptions.AllDisabled,
                        renderOptions = RenderOptions.Standard
                    )
            )
        Anchor(anchorLocation = anchorLocation, sreenWidth = maxWidth)

        EditMapButton(
            anchorLocation != null,
            onEditClick = onEditClick,
            modifier = Modifier.padding(bottom = 4.dp).align(Alignment.BottomEnd)
        )
    }
}
