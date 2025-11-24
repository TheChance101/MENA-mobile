package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location
import mena.identity_presentation.generated.resources.address
import mena.identity_presentation.generated.resources.edit_location
import mena.identity_presentation.generated.resources.ic_address
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthAppBar
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.components.AddressTypeSection
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.components.MapSection
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.components.OtherAddressType
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreen
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

class AddEditLocationScreen(
    private val addressModel: AddressUIState?,
    private val onAddLocationSuccess: () -> Unit
) : BaseScreen<
    AddEditLocationScreenViewModel,
    AddEditLocationScreenUIState,
    AddEditLocationScreenUIEffect,
    AddEditLocationScreenInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(addressModel) }))
    }

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun OnRender(
        state: AddEditLocationScreenUIState,
        listener: AddEditLocationScreenInteractionListener
    ) {
        val listState = rememberLazyListState()
        LaunchedEffect(state.addressUIState.addressType) {
            if (state.addressUIState.addressType is AddressType.Other && addressModel == null) {
                delay(300)
                listState.animateScrollToItem(3)

            }
        }
        Scaffold(
            topBar = {
                AuthAppBar(
                    title = if (state.addressUIState.addressID == null)
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
                state = listState,
                modifier = Modifier
                    .background(color = Theme.colorScheme.background.surface)
                    .padding(horizontal = Theme.spacing._16),
                contentPadding = PaddingValues(bottom = Theme.spacing._16)
            ) {

                item {
                    MapSection(
                        isMapClickable = state.addressUIState.addressDetails.isBlank(),
                        cameraPosition = state.cameraPosition,
                        onClickEdit = listener::onClickEdit,
                        onClickMap = listener::onClickMap,
                    )
                }

                item {
                    TextField(
                        value = state.addressUIState.addressDetails,
                        title = stringResource(Res.string.address),
                        onValueChanged = {},
                        readOnly = true,
                        enabled = false,
                        hint = "",
                        leadingIcon = painterResource(Res.drawable.ic_address),
                        modifier = Modifier.padding(top = Theme.spacing._12),
                    )
                }

                item {
                    AddressTypeSection(
                        selectedAddressType = state.addressUIState.addressType,
                        onClickAddressType = { newType ->
                            listener.onClickAddressType(newType)
                        },
                    )
                }

                item {
                    OtherAddressType(
                        selectedAddressType = state.addressUIState.addressType,
                        otherAddressType = state.addressUIState.otherAddressType,
                        onChangeOtherAddressType = listener::onChangeOtherAddressType,
                    )
                }

            }
        }
    }

    override fun onEffect(
        effect: AddEditLocationScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            is AddEditLocationScreenUIEffect.NavigateBack -> {
                effect.successStringResource?.let { successMessage ->
                    snackBarController.showSnackBarSuccess(
                        message = successMessage
                    )

                    onAddLocationSuccess()
                }

                effect.errorStringResource?.let { errorMessage ->
                    snackBarController.showSnackBarError(
                        message = errorMessage
                    )
                }

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



