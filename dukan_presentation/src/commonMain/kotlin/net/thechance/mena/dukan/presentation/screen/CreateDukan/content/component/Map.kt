package net.thechance.mena.dukan.presentation.screen.createDukan.content.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.Position
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.anchor
import mena.dukan_presentation.generated.resources.ic_edit
import net.thechance.mena.designsystem.presentation.component.image.MenaImage
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.map.MapStyle
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
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
fun Map(
    location: CreateDukanUiState.CoordinatesUiState,
    isLocked: Boolean,
    onMapClick: (CreateDukanUiState.CoordinatesUiState) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var markerOffset by remember { mutableStateOf<DpOffset?>(null) }

    val camera = rememberCameraState(firstPosition = CameraPosition())


    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition =
                camera.position.copy(
                    target = Position(latitude = location.latitude, longitude = location.longitude),
                    zoom = 11.0
                ),
            duration = 5.seconds,
        )
    }

    Box(
        modifier = modifier
    ) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = camera,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { position, offset ->
                if (isLocked) {
                    ClickResult.Consume
                } else {
                    markerOffset = offset
                    onMapClick(
                        CreateDukanUiState.CoordinatesUiState(
                            position.latitude,
                            position.longitude
                        )
                    )
                    ClickResult.Pass
                }
            },
            options =
                MapOptions(
                    gestureOptions = if (isLocked) {
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
                MenaImage(
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
                MenaImage(
                    modifier = Modifier
                        .padding(Theme.spacing._4)
                        .clip(RoundedCornerShape(Theme.radius.md))
                        .background(Color.Black)
                        .padding(
                            horizontal = Theme.spacing._16,
                            vertical = Theme.spacing._12
                        ).size(20.dp)
                        .clickable {
                            onEditClick()
                            markerOffset = null
                        },
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = null
                )
            }
        }
    }
}