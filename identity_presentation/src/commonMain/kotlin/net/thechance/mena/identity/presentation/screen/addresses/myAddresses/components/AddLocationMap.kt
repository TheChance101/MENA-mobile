package net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_anchor
import net.thechance.mena.identity.presentation.components.util.MapStyle
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddLocationMap(
    isMapClickable: Boolean,
    cameraPosition: CameraPosition,
    onEditClick: () -> Unit,
    onClickMap: () -> Unit,
    modifier: Modifier = Modifier
) {

    val camera = rememberCameraState(firstPosition = cameraPosition)

    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition = cameraPosition,
        )
    }

    Box(modifier = modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { _, _ ->
                if (isMapClickable) onClickMap()
                ClickResult.Consume
            },
            options =
                MapOptions(
                    gestureOptions = GestureOptions.AllDisabled,
                    ornamentOptions = OrnamentOptions.AllDisabled,
                    renderOptions = RenderOptions.Standard
                )
        )

        AnimatedVisibility (
            visible = !isMapClickable,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_anchor),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.padding(bottom = 58.dp).height(58.dp)
            )
        }

        AnimatedVisibility (
            visible = !isMapClickable,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            EditMapButton(
                onEditClick = onEditClick,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}
