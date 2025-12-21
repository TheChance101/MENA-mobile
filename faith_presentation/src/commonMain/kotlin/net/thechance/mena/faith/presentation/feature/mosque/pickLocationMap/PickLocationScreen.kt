package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.GpsFabButton
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.PickLocationMap
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PickLocationScreen(viewModel: PickLocationViewModel = koinViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is PickLocationScreenUIEffect.NavigateBack -> navController.popBackStack()
            is PickLocationScreenUIEffect.NavigateBackWithLocation -> {
                navController.previousBackStackEntry?.savedStateHandle?.apply {
                    set("address_model_json_string", effect.mosqueLocation.toAddressJsonString())
                    navController.popBackStack()
                }
            }
        }
    }
    Content(uiState = uiState, listener = viewModel)
}

@Composable
private fun Content(
    uiState: PickLocationScreenUIState,
    listener: PickLocationScreenInteractionListener
) {
    Scaffold(
        topBar = { PickLocationAppBar(onBackClick = listener::onClickBack) }
    ) {
        PickLocationMap(
            currentLocation = uiState.mosqueLocation,
            animateToCurrentLocation = uiState.animateToCurrentLocation,
            showAnchor = uiState.showAnchor,
            onClickMap = listener::onClickMap,
            onMoveCamera = listener::onMoveCamera,
        ) {
            Column(
                Modifier.padding(Theme.spacing._16).fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                GpsFabButton(
                    onClick = listener::onClickGps,
                    isLoading = uiState.isGpsButtonLoading,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                PrimaryButton(
                    text = stringResource(Res.string.confirm),
                    onClick = listener::onClickConfirm,
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