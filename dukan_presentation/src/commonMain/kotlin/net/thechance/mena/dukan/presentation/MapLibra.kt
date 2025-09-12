package net.thechance.mena.dukan.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import io.github.dellisd.spatialk.geojson.Position
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.anchor
import mena.dukan_presentation.generated.resources.pencil_edit_01
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.domain.entity.Dukan
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
fun MapLibra(
    location: Dukan.Location,
    modifier: Modifier = Modifier
) {

    var markerOffset by remember { mutableStateOf<DpOffset?>(null) }
    var locked by rememberSaveable { mutableStateOf(false) }
    var position by remember { mutableStateOf<Dukan.Location?>(null) }

    val camera = rememberCameraState(firstPosition = CameraPosition())

    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition =
                camera.position.copy(
                    target = Position(latitude = location.latitude, longitude = location.longitude),
                    zoom = 13.0
                ),
            duration = 3.seconds,
        )
        position?.let {
            println(
                "Location Name: ${
                    MobileGeocoder().placeOrNull(
                        Coordinates(
                            it.latitude,
                            it.longitude
                        )
                    )
                }"
            )
        }
    }

    Box(
        modifier = modifier
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

@Preview
@Composable
private fun Map() {
    MenaTheme {
        MapLibra(
            location = Dukan.Location(
                latitude = 47.607,
                longitude = -122.342,
                address = ""
            )
        )
    }
}