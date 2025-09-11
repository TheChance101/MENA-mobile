package net.thechance.mena.dukan.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dellisd.spatialk.geojson.Position
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.anchor
import mena.dukan_presentation.generated.resources.pencil_edit_01
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
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MapLibra() {
    var markerOffset by remember { mutableStateOf<DpOffset?>(null) }
    var locked by rememberSaveable { mutableStateOf(false) }

    val camera =
        rememberCameraState(
            firstPosition =
                CameraPosition(
                    target = Position(latitude = 45.521, longitude = -122.675),
                    zoom = 13.0
                )
        )
    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition =
                camera.position.copy(target = Position(latitude = 47.607, longitude = -122.342)),
            duration = 3.seconds,
        )
    }
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/bright"),
            onMapClick = { position, offset ->
                if (locked) {
                    ClickResult.Consume
                } else {
                    markerOffset = offset
                    locked = true
                    ClickResult.Pass
                }
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
        Crossfade(
            targetState = markerOffset
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

        Crossfade(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            targetState = markerOffset != null
        ) {
            if (it) {
                Image(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .size(20.dp)
                        .clickable {
                            locked = false
                            markerOffset = null
                        },
                    painter = painterResource(Res.drawable.pencil_edit_01),
                    contentDescription = null
                )
            }
        }
    }
}