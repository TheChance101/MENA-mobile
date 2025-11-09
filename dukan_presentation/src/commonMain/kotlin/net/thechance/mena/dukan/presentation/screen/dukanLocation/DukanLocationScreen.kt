package net.thechance.mena.dukan.presentation.screen.dukanLocation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
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
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle

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
    LaunchedEffect(Unit) {
        listener.onCameraMoved(state.cameraPosition)
    }
    Scaffold(
        topBar = { DukanLocationTopBar(onBackClick = listener::onBackClicked) }
    ) {
        val cameraState = rememberCameraState(state.cameraPosition)
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            options = MapOptions(
                gestureOptions = GestureOptions.Standard,
                ornamentOptions = OrnamentOptions.AllDisabled,
                renderOptions = RenderOptions.Standard
            )
        )
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