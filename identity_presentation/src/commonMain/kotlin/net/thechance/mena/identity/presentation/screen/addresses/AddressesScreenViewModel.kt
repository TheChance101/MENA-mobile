package net.thechance.mena.identity.presentation.screen.addresses

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_deleted_successfully
import net.thechance.mena.identity.domain.repository.AddressRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage


class AddressesScreenViewModel(
    private val addressRepository: AddressRepository,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO

) :
    BaseScreenModel<AddressesScreenUIState, AddressesScreenUIEffect>(AddressesScreenUIState()),
    AddressesScreenInteractionListener {
    init {
        getUserAddresses()
    }

    private fun getUserAddresses() {
        tryToExecute(
            function = { addressRepository.getUserAddresses().map { it.toUiState() } },
            onSuccess = { ::onGetUserAddressesSuccess },
            onError = { ::onErrorOccurred },
        )
    }

    private fun onGetUserAddressesSuccess(addresses: List<AddressUIState>) = updateState {
        copy(addresses = addresses)
    }

    override fun onBackButtonClicked() {
        sendNewEffect(AddressesScreenUIEffect.NavigateBack)
    }

    override fun onAddButtonClicked() = sendNewEffect(
        AddressesScreenUIEffect.NavigateToDetailsScreen(null)
    )

    override fun onEditAddressClicked(addressId: AddressUIState) =
        sendNewEffect(AddressesScreenUIEffect.NavigateToDetailsScreen(addressId))

    override fun onAddressClicked(addressId: Long) {
    }

    override fun onDeleteAddressClicked(addressId: Long) = updateState {
        copy(
            deleteAddressDialogUIState = DeleteAddressDialogUIState(
                isVisible = true,
                addressId = addressId
            ),
        )
    }

    override fun onConfirmDeleteAddress() {
        tryToExecute(
            function = { addressRepository.deleteAddress(state.value.addressToDelete!!) },
            onSuccess = {
                getUserAddresses()
                updateState {
                    copy(
                        snackBarUiState = SnackBarUiState(
                            snackBarType = SnackBarType.SUCCESS,
                            isVisible = true,
                            message = Res.string.address_deleted_successfully
                        )
                    )
                }
            },
            onError = { ::onErrorOccurred }
        )
    }

    override fun onDismissDeleteDialog() =
        updateState {
            copy(
                deleteAddressDialogUIState = DeleteAddressDialogUIState(
                    isVisible = false,
                )
            )
        }

    override fun onDismissSnackBar() =
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    isVisible = false,
                )
            )
        }

    private fun onErrorOccurred(errorState: ErrorState) {
        updateState {
            copy(
                errorMessage = mapErrorToMessage(errorState)
            )
        }
    }

    override fun clearErrorMessage() = updateState { copy(errorMessage = null) }
}

