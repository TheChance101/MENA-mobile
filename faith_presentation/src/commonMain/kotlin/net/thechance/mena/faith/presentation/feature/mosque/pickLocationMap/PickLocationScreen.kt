package net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.confirm
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.GpsFabButton
import net.thechance.mena.faith.presentation.feature.mosque.pickLocationMap.component.PickLocationMap
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PickLocationScreen(viewModel: PickLocationViewModel = koinViewModel()) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is PickLocationScreenUIEffect.NavigateBack -> navController.popBackStack()
            is PickLocationScreenUIEffect.NavigateBackWithLocation -> {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_location", effect.mosqueLocation) //todo add location args to it
                navController.popBackStack()
            }
        }
    }

    PickLocationMap(
        currentLocation = state.mosqueLocation,
        animateToCurrentLocation = state.animateToCurrentLocation,
        showAnchor = state.showAnchor,
        onClickMap = viewModel::onClickMap,
        onMoveCamera = viewModel::onMoveCamera,
    ) {
        Column(
            Modifier
                .padding(Theme.spacing._16)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            GpsFabButton(
                onClick = viewModel::onClickGps,
                isLoading = state.isGpsButtonLoading,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            PrimaryButton(
                text = stringResource(Res.string.confirm),
                onClick = viewModel::onClickConfirm,
                isEnabled = state.isConfirmEnabled,
                contentPadding = PaddingValues(vertical = 13.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}
