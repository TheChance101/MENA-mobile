package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.confirmation_dialog_delete
import mena.identity_presentation.generated.resources.my_location_app_bar_title
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AddressSnackBar
import net.thechance.mena.identity.presentation.components.NoSavedLocationsLayout
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreen
import net.thechance.mena.identity.presentation.screen.addresses.components.AddressCard
import net.thechance.mena.identity.presentation.screen.addresses.components.MyAddressesAppBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

class AddressesScreen :
    BaseScreen<AddressesScreenViewModel, AddressesScreenUIState, AddressesScreenUIEffect, AddressesScreenInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun OnRender(
        state: AddressesScreenUIState, listener: AddressesScreenInteractionListener
    ) {
        Scaffold(
            overlays = {
                dialog(state.deleteDialogUIState.isVisible) {
                    Dialog(
                        isVisible = it,
                        title = stringResource(state.deleteDialogUIState.title),
                        message = stringResource(state.deleteDialogUIState.description),
                        onDismiss = listener::onDismissDeleteDialog,
                        onCancelClick = listener::onDismissDeleteDialog,
                        actionButtons = {
                            TextButton(
                                modifier = Modifier
                                    .padding(
                                        vertical = Theme.spacing._24,
                                        horizontal = Theme.spacing._8
                                    )
                                    .align(Alignment.End),
                                text = stringResource(Res.string.confirmation_dialog_delete),
                                onClick = listener::onConfirmDeleteAddress,
                                iconSize = Theme.spacing._16,
                                contentColor = Theme.colorScheme.error
                            )
                        }
                    )
                }
            },
            snakeBar = {
                AddressSnackBar(
                    snackBarState = state.snackBarUiState,
                    onDismiss = listener::onDismissSnackBar,
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Theme.spacing._16),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
            ) {
                item {
                    MyAddressesAppBar(
                        title = stringResource(Res.string.my_location_app_bar_title),
                        onBackClicked = listener::onBackButtonClicked,
                        onAddClicked = listener::onAddButtonClicked
                    )
                }
                items(state.addresses) {
                    AddressCard(
                        addressType = it.addressType,
                        onEditClick = { listener.onEditAddressClicked(it) },
                        isMainAddress = it.isMainAddress,
                        addressDetails = it.addressDetails,
                        onDeleteClick = { it.id?.let { id -> listener.onDeleteAddressClicked(id) } },
                        onClickAddress = { it.id?.let { id -> listener.onClickAddress(id) } },
                        animateToCurrentLocation = state.animateToCurrentLocation,
                        longitude = it.coordinates.longitude,
                        latitude = it.coordinates.latitude,
                    )
                }
            }
        }

        if (state.addresses.isEmpty() && !state.isLoading) {
            NoSavedLocationsLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                onAddLocationClicked = listener::onAddButtonClicked
            )
        }
    }

    override fun onEffect(
        effect: AddressesScreenUIEffect,
        navigator: Navigator
    ) {
        when (effect) {
            AddressesScreenUIEffect.NavigateBack -> navigator.pop()
            is AddressesScreenUIEffect.NavigateToAddressDetailsScreen -> {
                navigator.push(
                    AddEditLocationScreen(
                        addressModel = effect.addressUIState,
                        onSuccess = effect.onSuccess
                    )
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewRegisterScreen() {
    MenaTheme {
        AddressesScreen().Content()
    }
}