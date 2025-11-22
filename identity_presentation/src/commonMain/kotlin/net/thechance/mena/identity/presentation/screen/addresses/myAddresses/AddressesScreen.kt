package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.my_location_app_bar_title
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.NoSavedLocationsLayout
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreen
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components.AddressCard
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components.AddressCardShimmer
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components.AddressShimmerPlaceholders
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components.MyAddressesAppBar
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.components.deleteAddressDialog
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddressesScreen(
    private val onNavigateBack: (() -> Unit)? = null
) : BaseScreen<
    AddressesScreenViewModel,
    AddressesScreenUIState,
    AddressesScreenUIEffect,
    AddressesScreenInteractionListener>() {
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
                deleteAddressDialog(
                    deleteDialogUIState = state.deleteDialogUIState,
                    onDismissDeleteDialog = listener::onDismissDeleteDialog,
                    onConfirmDeleteAddress = listener::onConfirmDeleteAddress
                )
            },
            topBar = {
                MyAddressesAppBar(
                    title = stringResource(Res.string.my_location_app_bar_title),
                    onBackClicked = listener::onBackButtonClicked,
                    onAddClicked = listener::onAddButtonClicked
                )
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = state.addresses.isNotEmpty() && !state.isLoading,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    AddressesSection(
                        addresses = state.addresses,
                        onEditAddressClicked = listener::onEditAddressClicked,
                        onDeleteAddressClicked = listener::onDeleteAddressClicked,
                        onClickAddress = listener::onClickAddress,
                        animateToCurrentLocation = state.animateToCurrentLocation,
                        isAddingNewAddress = state.isAddingNewAddress
                    )
                }

                AnimatedVisibility(
                    visible = state.addresses.isEmpty() && !state.isLoading && state.errorMessage == null,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    NoSavedLocationsLayout(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
                        onAddLocationClicked = listener::onAddButtonClicked
                    )
                }

                AnimatedVisibility(
                    visible = state.isLoading && state.addresses.isEmpty(),
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    AddressShimmerPlaceholders()
                }
            }
        }
    }

    override fun onEffect(
        effect: AddressesScreenUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            AddressesScreenUIEffect.NavigateBack -> {
                onNavigateBack?.invoke() ?: navigator.pop()
            }

            is AddressesScreenUIEffect.NavigateToAddressDetailsScreen -> {
                navigator.push(
                    AddEditLocationScreen(
                        addressModel = effect.addressUIState,
                    )
                )
            }

            is AddressesScreenUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }

            is AddressesScreenUIEffect.ShowSnackBarSuccess -> {
                snackBarController.showSnackBarSuccess(
                    message = effect.successStringResource
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun AddressesSection(
    addresses: List<AddressUIState>,
    onEditAddressClicked: (AddressUIState) -> Unit,
    onDeleteAddressClicked: (Uuid) -> Unit,
    onClickAddress: (Uuid) -> Unit,
    animateToCurrentLocation: Boolean,
    isAddingNewAddress: Boolean = false
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
        contentPadding = PaddingValues(bottom = Theme.spacing._16)
    ) {
        items(
            items = addresses,
            key = { it.id.toString() }
        ) {
            AddressCard(
                addressType = it.addressType,
                onEditClick = { onEditAddressClicked(it) },
                isMainAddress = it.isMainAddress,
                addressDetails = it.addressDetails,
                onDeleteClick = { it.id?.let { id -> onDeleteAddressClicked(id) } },
                onClickAddress = { it.id?.let { id -> onClickAddress(id) } },
                animateToCurrentLocation = animateToCurrentLocation,
                longitude = it.coordinates.longitude,
                latitude = it.coordinates.latitude,
                isDeleting = it.isDeleting,
                isActivating = it.isActivating || it.isRefreshing
            )
        }

        if (isAddingNewAddress) {
            item {
                AddressCardShimmer()
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