package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

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
import net.thechance.mena.identity.presentation.screen.addresses.AddressUIState
import net.thechance.mena.identity.presentation.screen.enableLocationScreen.EnableLocationScreen
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.EditMapButton
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.GpsFabButton
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.PickLocationMap
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

data class PickLocationScreen(
    private val addressModel: AddressUIState?,
    private val onUpdateLocation: (AddressUIState) -> Unit,
) : BaseScreen<PickLocationScreenViewModel,
        PickLocationScreenUIState,
        PickLocationScreenUIEffect,
        PickLocationScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(addressModel) }))
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
            },
        ) {

            PickLocationMap(
                cameraPosition = state.cameraPosition,
                onCameraMoved = listener::onMoveCamera,
                onMapClick = listener::onClickMap,
                anchorLocation = state.pointerLocation,
                isLocked = state.isMapLocked,
                currentLocation = state.currentLocation,
                animateToCurrentLocation = state.animateToCurrentLocation,
                onSetAnchorLocation = listener::onSetAnchorLocation,
            ) {
                Column(
                    Modifier.padding(Theme.spacing._16).fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GpsFabButton(
                            onClick = listener::onClickGps,
                            isLoading = state.isGpsButtonLoading,
                        )
                        EditMapButton(
                            isMapLocked = state.isMapLocked,
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
            errorMessage = state.errorMessage?.let { stringResource(it) },
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
            is PickLocationScreenUIEffect.NavigateBackWithLocation -> {
                onUpdateLocation(effect.addressModel)
                navigator.pop()
            }

            PickLocationScreenUIEffect.NavigateToEnableLocation -> navigator.push(
                EnableLocationScreen()
            )
        }
    }
}