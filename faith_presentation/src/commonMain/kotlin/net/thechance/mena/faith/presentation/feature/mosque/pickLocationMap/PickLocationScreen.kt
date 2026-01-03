package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.arrow_left
import mena.faith_presentation.generated.resources.confirm
import mena.faith_presentation.generated.resources.pick_location_title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.GpsFabButton
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.PickLocationMap
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PickLocationScreen(viewModel: PickLocationViewModel = koinViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is PickLocationScreenUIEffect.NavigateBack -> navController.popBackStack()
            is PickLocationScreenUIEffect.NavigateBackWithLocation -> {
                navController.previousBackStackEntry?.savedStateHandle?.apply {
                    set("address_model_json_string", effect.mosqueLocation.toAddressJsonString())
                    navController.navigateUp()
                }
            }
        }
    }
    Content(uiState = uiState, snackBarState = snackBarState, listener = viewModel)
}

@Composable
private fun Content(
    uiState: PickLocationScreenUIState,
    snackBarState: SnackBarState,
    listener: PickLocationScreenInteractionListener
) {
    Scaffold(
        topBar = { PickLocationAppBar(onBackClick = listener::onBackClick) },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                isVisible = snackBarState.isVisible,
                status = snackBarState.status,
            )
        },
        statusBarColor = Theme.colorScheme.background.surfaceLow,
    ) {
        PickLocationMap(
            currentLocation = uiState.mosqueLocation,
            animateToCurrentLocation = uiState.animateToCurrentLocation,
            showAnchor = uiState.showAnchor,
            onMapClick = listener::onMapClick,
            onMoveCamera = listener::onMoveCamera,
        ) {
            Column(
                Modifier.padding(Theme.spacing._16).fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                GpsFabButton(
                    onClick = listener::onGpsClick,
                    isLoading = uiState.isGpsButtonLoading,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                PrimaryButton(
                    text = stringResource(Res.string.confirm),
                    onClick = listener::onConfirmClick,
                    isEnabled = uiState.isConfirmEnabled,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PickLocationAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().background(Theme.colorScheme.background.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        AppBar(
            modifier = modifier,
            title = stringResource(Res.string.pick_location_title),
            leadingContent = {
                Icon(
                    painter = painterResource(Res.drawable.arrow_left),
                    contentDescription = stringResource(Res.string.arrow_left),
                    tint = Theme.colorScheme.primary.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            onLeadingClick = onBackClick,
        )
    }
}