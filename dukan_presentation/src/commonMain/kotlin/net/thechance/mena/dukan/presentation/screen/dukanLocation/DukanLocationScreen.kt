package net.thechance.mena.dukan.presentation.screen.dukanLocation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.anchor
import mena.dukan_presentation.generated.resources.back_to_dukan_screen_icon
import mena.dukan_presentation.generated.resources.dukan_location
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.map.MapStyle
import net.thechance.mena.dukan.presentation.viewModel.dukanLocation.DukanLocationEffect
import net.thechance.mena.dukan.presentation.viewModel.dukanLocation.DukanLocationInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanLocation.DukanLocationUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanLocation.DukanLocationViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import kotlin.time.Duration.Companion.seconds

@Composable
fun DukanLocationScreen(
    viewModel: DukanLocationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            DukanLocationEffect.NavigateBack -> navController.popBackStack()
        }
    }
    DukanLocationContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun DukanLocationContent(
    state: DukanLocationUiState,
    listener: DukanLocationInteractionListener
) {
    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = { DukanLocationTopBar(onBackClick = listener::onBackClicked) }
    ) {
        val cameraState = rememberCameraState(CameraPosition())
        var isAnchorVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            cameraState.animateTo(
                finalPosition = cameraState.position.copy(
                    target = state.cameraPosition.target,
                    zoom = state.cameraPosition.zoom
                ),
                duration = 2.seconds
            )
        }
        LaunchedEffect(Unit) {
            delay(1500)
            isAnchorVisible = true
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
                options = MapOptions(
                    gestureOptions = GestureOptions.AllDisabled,
                    ornamentOptions = OrnamentOptions.AllDisabled,
                    renderOptions = RenderOptions.Standard
                )
            )
            AnimatedVisibility(
                visible = isAnchorVisible
            ) {
                if (isAnchorVisible) {
                    Image(
                        painter = painterResource(Res.drawable.anchor),
                        contentDescription = null,
                        modifier = Modifier.size(46.dp, 58.05.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun DukanLocationTopBar(onBackClick: () -> Unit) {
    AppBar(
        title = stringResource(resource = Res.string.dukan_location),
        titleColor = Theme.colorScheme.shadePrimary,
        leadingContent = {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_arrow_left),
                contentDescription = stringResource(resource = Res.string.back_to_dukan_screen_icon),
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = onBackClick,
    )
}