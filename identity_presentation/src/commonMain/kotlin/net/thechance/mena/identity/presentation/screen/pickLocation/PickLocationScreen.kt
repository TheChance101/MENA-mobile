package net.thechance.mena.identity.presentation.screen.pickLocation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirm
import mena.identity_presentation.generated.resources.pick_location_title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
import net.thechance.mena.identity.presentation.screen.addresses.AddEditLocationScreen
import net.thechance.mena.identity.presentation.screen.enableLocationScreen.EnableLocationScreen
import net.thechance.mena.identity.presentation.screen.pickLocation.components.EditMapButton
import net.thechance.mena.identity.presentation.screen.pickLocation.components.GpsFabButton
import net.thechance.mena.identity.presentation.screen.pickLocation.components.Map
import org.jetbrains.compose.resources.stringResource

class PickLocationScreen() : BaseScreen<PickLocationScreenViewModel,
        PickLocationScreenUIState,
        PickLocationScreenUIEffect,
        PickLocationScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: PickLocationScreenUIState,
        listener: PickLocationScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.pick_location_title),
                    onClickBack = listener::onClickBack,
                    backgroundColor = Theme.colorScheme.background.surfaceLow
                )
            }
        ) {
            Map(
                cameraPosition = state.cameraPosition,
                onCameraMoved = listener::onMoveCamera,
                onMapClick = listener::onClickMap,
                anchorLocation = state.pointerLocation,
                isLocked = state.isMapLocked,
                currentLocation = state.currentLocation,
                animateToCurrentLocation = state.animateToCurrentLocation
            ) {
                Column(
                    Modifier.padding(Theme.spacing._16).fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GpsFabButton(
                            onClick = listener::onClickGps,
                            isLoading = state.isGpsButtonLoading,
                            modifier = Modifier.padding(bottom = Theme.spacing._12)
                        )
                        EditMapButton(
                            anchorLocation = state.pointerLocation,
                            onEditClick = listener::onClickEdit
                        )
                    }

                    PrimaryButton(
                        text = stringResource(Res.string.confirm),
                        onClick = listener::onClickConfirm,
                        isEnabled = state.isConfirmEnabled,
                        isLoading = state.isLoading,
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
        }
        ErrorSnackBar(
            errorMessage = state.errorMessage,
            onDismiss = listener::onClearErrorMessage,
            modifier = Modifier.statusBarsPadding()
        )
    }

    override fun onEffect(
        effect: PickLocationScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            PickLocationScreenUIEffect.NavigateBack -> navigator.pop()
            is PickLocationScreenUIEffect.NavigateToAddLocation -> navigator.replace(
                AddEditLocationScreen(effect.latitude, effect.longitude, effect.address)
            )

            PickLocationScreenUIEffect.NavigateToEnableLocation -> navigator.push(
                EnableLocationScreen()
            )
        }
    }
}