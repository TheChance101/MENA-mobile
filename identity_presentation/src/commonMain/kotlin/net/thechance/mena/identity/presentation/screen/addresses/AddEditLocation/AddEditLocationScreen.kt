package net.thechance.mena.identity.presentation.screen.addresses.AddEditLocation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location
import mena.identity_presentation.generated.resources.address
import mena.identity_presentation.generated.resources.address_type
import mena.identity_presentation.generated.resources.edit_location
import mena.identity_presentation.generated.resources.ic_add_location
import mena.identity_presentation.generated.resources.ic_address
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AddressTypeSection
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.screen.addresses.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.SnackBarUiState
import net.thechance.mena.identity.presentation.screen.addresses.component.MapSection
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class AddEditLocationScreen(
    val onSuccess: (SnackBarUiState?) -> Unit,
    private val addressModel: AddressUIState?,

    ) : BaseScreen<
        AddEditLocationScreenViewModel,
        AddLocationScreenUIState,
        AddEditLocationScreenUIEffect,
        AddEditLocationScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(addressModel) }))
    }

    @Composable
    override fun OnRender(
        state: AddLocationScreenUIState,
        listener: AddEditLocationScreenInteractionListener
    ) {
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = if (state.addressID == null)
                        stringResource(Res.string.add_location)
                    else
                        stringResource(Res.string.edit_location),
                    onClickBack = listener::onClickBack
                )
            },
            bottomBar = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = Theme.spacing._16)
                        .padding(bottom = Theme.spacing._16),
                    text = stringResource(Res.string.save),
                    onClick = listener::onClickSave,
                    isEnabled = state.isSaveEnabled,
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(vertical = Theme.spacing._12)
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(color = Theme.colorScheme.background.surface)
                    .padding(horizontal = Theme.spacing._16),
                contentPadding = PaddingValues(bottom = Theme.spacing._16)
            ) {

                item {
                    MapSection(
                        cameraPosition = state.cameraPosition,
                        onClickEdit = listener::onClickEdit,
                        onClickMap = listener::onClickMap,
                        anchorLocation = state.anchorLocation,
                        setAnchorLocation = listener::onSetAnchorLocation,
                        longitude = state.longitude,
                        latitude = state.latitude,
                        animateToCurrentLocation = state.animateToCurrentLocation
                    )
                }

                item {
                    TextField(
                        value = state.address,
                        title = stringResource(Res.string.address),
                        onValueChanged = { newAddress ->
                            listener.onChangeAddress(newAddress)
                        },
                        readOnly = true,
                        enabled = false,
                        hint = "",
                        leadingIcon = painterResource(Res.drawable.ic_address),
                        modifier = Modifier.padding(top = Theme.spacing._12),
                    )
                }

                item {
                    AddressTypeSection(
                        selectedAddressType = state.addressType,
                        onClickAddressType = { newType ->
                            listener.onClickAddressType(newType)
                        },
                        addressType = state.otherAddress?:""

                    )
                }

                item {
                    OtherAddressType(
                        selectedAddressType = state.addressType,
                        otherAddressType = state.otherAddress,
                        onChangeOtherAddressType = listener::onChangeOtherAddressType,
                    )
                }

            }
        }
    }

    override fun onEffect(
        effect: AddEditLocationScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            is AddEditLocationScreenUIEffect.NavigateBack -> {
                onSuccess(effect.snackBarUiState)
                navigator.pop()
            }

            is AddEditLocationScreenUIEffect.NavigateToMap -> navigator.push(
                PickLocationScreen(
                    addressModel = effect.addressModel,
                    onUpdateLocation = effect.onUpdateLocation
                )
            )
        }
    }


}

@Composable
private fun OtherAddressType(
    selectedAddressType: AddressType?,
    otherAddressType: String?,
    onChangeOtherAddressType: (String) -> Unit,
    modifier: Modifier = Modifier,

    ) {
    AnimatedVisibility(
        visible = (otherAddressType != null),
        enter = expandVertically(
            animationSpec = tween(durationMillis = 500)
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 500)
        )
    ) {

        TextField(
            value = otherAddressType ?: "",
            onValueChanged = onChangeOtherAddressType,
            title = stringResource(Res.string.address_type),
            hint = "",
            leadingIcon = painterResource(Res.drawable.ic_add_location),
            modifier = modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12)
        )
    }
}


