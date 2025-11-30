package net.thechance.mena.dukan.presentation.screen.createDukan.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.anchor
import mena.dukan_presentation.generated.resources.ic_edit
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.map.MapStyle
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
fun Map(
    anchorLocation: DpOffset?,
    isLocked: Boolean,
    cameraPosition: CameraPosition,
    onCameraMoved: (CameraPosition) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var locked by remember { mutableStateOf(isLocked) }
    val camera = rememberCameraState(firstPosition = cameraPosition)
    var screenSize by remember { mutableStateOf(Pair(0.dp, 0.dp)) }

    LaunchedEffect(isLocked) {
        locked = isLocked
    }

    LaunchedEffect(cameraPosition) {
        camera.animateTo(
            finalPosition = cameraPosition,
        )
    }
    LaunchedEffect(camera) {
        snapshotFlow { camera.position }
            .collect { position ->
                onCameraMoved(position)
            }
    }

    BoxWithConstraints(
        modifier = modifier
    ) {

        LaunchedEffect(maxWidth, maxHeight) {
            if (maxWidth != screenSize.first || maxHeight != screenSize.second) {
                screenSize = Pair(maxWidth, maxHeight)
                if (!isLocked) {
                    locked = false
                }
            }
        }
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { _, _ ->
                if (!locked) {
                    onEditClick()
                }
                ClickResult.Pass
            },
            options =
                MapOptions(
                    gestureOptions = if (locked) {
                        GestureOptions.AllDisabled
                    } else {
                        GestureOptions.Standard
                    },
                    ornamentOptions = OrnamentOptions.AllDisabled,
                    renderOptions = RenderOptions.Standard
                )
        )
        val displayAnchor =
            if (locked && (anchorLocation == null || (anchorLocation.x == 0.dp && anchorLocation.y == 0.dp))) {
                DpOffset(maxWidth / 2, maxHeight / 2)
            } else {
                anchorLocation
            }

        Crossfade(
            targetState = displayAnchor
        ) {
            it?.let { offset ->
                Image(
                    painter = painterResource(Res.drawable.anchor),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp, 58.05.dp)
                        .offset(
                            x = offset.x - 23.dp,
                            y = offset.y - 50.05.dp
                        )
                )
            }
        }
        if (locked) {
            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Theme.spacing._4)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .clickable {
                        onEditClick()
                        locked = false
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