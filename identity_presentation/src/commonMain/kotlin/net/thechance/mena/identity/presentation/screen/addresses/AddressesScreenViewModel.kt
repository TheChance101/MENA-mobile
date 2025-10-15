package net.thechance.mena.identity.presentation.screen.addresses

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_deleted_successfully
import mena.identity_presentation.generated.resources.unexpected_error
import net.thechance.mena.identity.domain.repository.AddressRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AddressesScreenViewModel(
    private val addressRepository: AddressRepository,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO

) : BaseScreenModel<AddressesScreenUIState, AddressesScreenUIEffect>(AddressesScreenUIState()),
    AddressesScreenInteractionListener {
    init {
        getUserAddresses()
    }

    override fun onBackButtonClicked() = sendNewEffect(AddressesScreenUIEffect.NavigateBack)

    override fun onAddButtonClicked() = sendNewEffect(
        AddressesScreenUIEffect.NavigateToAddressDetailsScreen(null)
    )

    override fun onEditAddressClicked(addressUIState: AddressUIState) =
        sendNewEffect(AddressesScreenUIEffect.NavigateToAddressDetailsScreen(addressUIState))


    override fun onClickAddress(addressId: Uuid) {
    }

    override fun onDeleteAddressClicked(addressId: Uuid) = updateState {
        copy(
            deleteDialogUIState = DeleteDialogUIState(
                isVisible = true, addressId = addressId
            ),
        )
    }

    override fun onConfirmDeleteAddress() {
        tryToExecute(
            function = { addressRepository.deleteAddress(state.value.deleteDialogUIState.addressId!!) },
            onSuccess = {
                getUserAddresses()
                updateState {
                    copy(
                        snackBarUiState = SnackBarUiState(
                            snackBarType = SnackBarType.SUCCESS,
                            isVisible = true,
                            message = Res.string.address_deleted_successfully
                        ),
                        deleteDialogUIState = DeleteDialogUIState(
                            isVisible = false,
                        )
                    )
                }
            },
            onError = ::onErrorOccurred,
            dispatcher = dispatcher
        )
    }

    override fun onDismissDeleteDialog() = updateState {
        copy(
            deleteDialogUIState = DeleteDialogUIState(
                isVisible = false,
            )
        )
    }

    override fun onDismissSnackBar() = updateState {
        copy(
            snackBarUiState = SnackBarUiState(
                isVisible = false,
            )
        )
    }

    private fun getUserAddresses() {
        tryToExecute(
            function = { addressRepository.getUserAddresses().map { it.toUiState() } },
            onSuccess = ::onGetUserAddressesSuccess,
            onError = ::onErrorOccurred,
            dispatcher = dispatcher,
        )
    }

    private fun onGetUserAddressesSuccess(addresses: List<AddressUIState>) = updateState {
        copy(addresses = addresses)
    }

    private fun onErrorOccurred(errorState: ErrorState) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    snackBarType = SnackBarType.ERROR,
                    isVisible = true,
                    message = Res.string.unexpected_error
                ),
                errorMessage = mapErrorToMessage(errorState)
            )
        }
    }

}

