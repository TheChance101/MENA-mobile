package net.thechance.mena.identity.presentation.screen.addresses.pickLocation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen.EnableLocationScreen
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.GpsFabButton
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.components.PickLocationMap
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

data class PickLocationScreen(
    private val addressModel: AddressUIState?,
    private val onUpdateLocation: (AddressUIState) -> Unit,
) : BaseScreen<
    PickLocationScreenViewModel,
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
            statusBarColor = Theme.colorScheme.background.surfaceLow,
            topBar = {
                AuthAppBar(
                    title = stringResource(Res.string.pick_location_title),
                    onClickBack = listener::onClickBack,
                    backgroundColor = Theme.colorScheme.background.surfaceLow
                )
            },
        ) {
            PickLocationMap(
                currentLocation = state.currentLocation,
                animateToCurrentLocation = state.animateToCurrentLocation,
                showAnchor = state.showAnchor,
                onClickMap = listener::onClickMap,
                onMoveCamera = listener::onMoveCamera,
            ) {
                Column(
                    Modifier.padding(Theme.spacing._16).fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    GpsFabButton(
                        onClick = listener::onClickGps,
                        isLoading = state.isGpsButtonLoading,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
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
    }

    override fun onEffect(
        effect: PickLocationScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
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

            is PickLocationScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    effect.errorStringResource
                )
            }
        }
    }
}