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
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.ErrorSnackBar
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
                    title = "Pick Location",
                    onClickBack = listener::onClickBack
                )
            }
        ) {
            Map(
                cameraPosition = state.cameraPosition,
                onCameraMoved = listener::onCameraMoved,
                onMapClick = listener::onClickMap,
                anchorLocation = state.pointerLocation,
                isLocked = state.isMapLocked,
                currentLocation = state.currentLocation,
                animateToCurrentLocation = state.animateToCurrentLocation
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GpsFabButton(
                            onClick = listener::onClickGps,
                            isLoading = state.isGpsButtonLoading,
                            modifier = Modifier.padding(bottom = 12.dp)
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
            is PickLocationScreenUIEffect.NavigateToAddLocation -> TODO("add navigator.replace(AddLocationScreen(effect.latitude, effect.longitude,effect.address}) when implement")
            PickLocationScreenUIEffect.NavigateToEnableLocation -> navigator.push(
                EnableLocationScreen()
            )
        }
    }
}