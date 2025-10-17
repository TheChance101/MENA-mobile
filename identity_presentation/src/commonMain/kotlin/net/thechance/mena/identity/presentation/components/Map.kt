package net.thechance.mena.identity.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_edit
import mena.identity_presentation.generated.resources.mena_location_marker
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
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
import sv.lib.squircleshape.SquircleShape

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Map(
    cameraPosition: CameraPosition,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val camera = rememberCameraState(firstPosition = cameraPosition)
    var screenSize by rememberSaveable { mutableStateOf(Pair(0.dp, 0.dp)) }

    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition = cameraPosition,
        )
    }

    BoxWithConstraints(
        modifier = modifier
    ) {

        LaunchedEffect(maxWidth, maxHeight) {
            if (maxWidth != screenSize.first || maxHeight != screenSize.second) {
                screenSize = Pair(maxWidth, maxHeight)
                onEditClick()
            }
        }

        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { position, offset ->
                ClickResult.Consume
            },
            options =
                MapOptions(
                    gestureOptions = GestureOptions.AllDisabled,
                    ornamentOptions = OrnamentOptions.AllDisabled,
                    renderOptions = RenderOptions.Standard
                )
        )
        Crossfade(
            targetState = cameraPosition.target.latitude != 28.0
                    && cameraPosition.target.longitude != 29.0,
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (it) {
                Image(
                    painter = painterResource(Res.drawable.mena_location_marker),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp, 58.05.dp)
                )
            }
        }
        Crossfade(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            targetState = cameraPosition.target.latitude != 28.0
                    && cameraPosition.target.longitude != 29.0
        ) {
            if (it) {
                Image(
                    modifier = Modifier
                        .padding(Theme.spacing._4)
                        .clip(SquircleShape(Theme.radius.md))
                        .clickable {
                            onEditClick()
                        }
                        .background(Color.Black)
                        .padding(
                            horizontal = Theme.spacing._16,
                            vertical = Theme.spacing._12
                        )
                        .size(20.dp),
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = null
                )
            }
        }
    }
}
