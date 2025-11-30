package net.thechance.mena.dukan.presentation.screen.createDukan.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.anchor
import mena.dukan_presentation.generated.resources.confirm
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.map.MapStyle
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.toCoordinatesUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
fun DukanLocationPicker(
    currentLocation: CreateDukanUiState.CoordinatesUiState,
    cameraPosition: CameraPosition,
    listener: CreateDukanInteractionListener,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val camera = rememberCameraState(
        firstPosition = cameraPosition
    )

    val initialLocation = if (currentLocation.latitude == 0.0 && currentLocation.longitude == 0.0) {
        cameraPosition.target.toCoordinatesUiState()
    } else {
        currentLocation
    }

    var tempLocation by remember { mutableStateOf(initialLocation) }

    LaunchedEffect(camera) {
        snapshotFlow { camera.isCameraMoving }
            .distinctUntilChanged()
            .filter { isMoving -> !isMoving }
            .drop(1)
            .collect {
                tempLocation = camera.position.target.toCoordinatesUiState()
            }
    }

    Scaffold(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                MaplibreMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraState = camera,
                    baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
                    options = MapOptions(
                        gestureOptions = GestureOptions.Standard,
                        ornamentOptions = OrnamentOptions.AllDisabled,
                        renderOptions = RenderOptions.Standard
                    ),
                    onMapClick = { position, _ ->
                        tempLocation = position.toCoordinatesUiState()
                        scope.launch {
                            camera.animateTo(
                                finalPosition = camera.position.copy(
                                    target = position
                                )
                            )
                        }
                        ClickResult.Pass
                    }
                )

                Image(
                    painter = painterResource(Res.drawable.anchor),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 58.dp)
                        .height(58.dp)
                )
            }

            PrimaryButton(
                text = stringResource(Res.string.confirm),
                onClick = {
                    listener.onMapClicked(
                        tempLocation,
                        DpOffset(0.dp, 0.dp)
                    )
                    listener.onConfirmLocationPicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing._16),
                contentPadding = PaddingValues(vertical = 13.dp)
            )
        }
    }
}
