package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_activated_successfully
import mena.identity_presentation.generated.resources.address_deleted_successfully
import mena.identity_presentation.generated.resources.is_main_address_error
import mena.identity_presentation.generated.resources.unexpected_error
import net.thechance.mena.identity.domain.exception.IsActiveAddress
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.mapper.toEntity
import net.thechance.mena.identity.presentation.screen.addresses.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.AddressesScreenUIState
import net.thechance.mena.identity.presentation.screen.addresses.DeleteDialogUIState
import net.thechance.mena.identity.presentation.screen.addresses.SnackBarType
import net.thechance.mena.identity.presentation.screen.addresses.SnackBarUiState
import net.thechance.mena.identity.presentation.screen.addresses.toUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
class AddressesScreenViewModel(
    private val addressesRepository: AddressesRepository,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO

) : BaseScreenModel<AddressesScreenUIState, AddressesScreenUIEffect>(AddressesScreenUIState()),
    AddressesScreenInteractionListener {
    init {
        getUserAddresses()
    }

    override fun onBackButtonClicked() = sendNewEffect(AddressesScreenUIEffect.NavigateBack)

    override fun onAddButtonClicked() = sendNewEffect(
        AddressesScreenUIEffect.NavigateToAddressDetailsScreen(
            addressUIState = null,
            onSuccess = { onAddEditSuccess(it) })
    )

    override fun onEditAddressClicked(addressUIState: AddressUIState) =
        sendNewEffect(
            AddressesScreenUIEffect.NavigateToAddressDetailsScreen(
                addressUIState = addressUIState,
                onSuccess = {
                    onAddEditSuccess(it)
                })
        )

    override fun onClickAddress(addressId: Uuid) {
        tryToExecute(
            function = {
                if (state.value.addresses.find { it.id == addressId }?.isMainAddress == false) {
                    addressesRepository.editAddress(
                        state.value.addresses.find { it.id == addressId }!!.toEntity()
                            .copy(isActive = true)
                    )
                }
            },
            onSuccess = {
                getUserAddresses()
                updateState {
                    copy(
                        snackBarUiState = SnackBarUiState(
                            snackBarType = SnackBarType.SUCCESS,
                            isVisible = true,
                            message = Res.string.address_activated_successfully
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

    override fun onDeleteAddressClicked(addressId: Uuid) = updateState {
        copy(
            deleteDialogUIState = DeleteDialogUIState(
                isVisible = true, addressId = addressId
            ),
        )
    }

    override fun onConfirmDeleteAddress() {
        tryToExecute(
            function = {
                val address =
                    state.value.addresses.find { it.id == state.value.deleteDialogUIState.addressId }
                if (address?.isMainAddress == true) {
                    throw IsActiveAddress()
                }
                addressesRepository.deleteAddress(state.value.deleteDialogUIState.addressId!!)
            },
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
            function = { addressesRepository.getUserAddresses().map { it.toUiState() } },
            onSuccess = ::onGetUserAddressesSuccess,
            onError = ::onErrorOccurred,
            dispatcher = dispatcher,
        )
    }

    private fun onGetUserAddressesSuccess(addresses: List<AddressUIState>) = updateState {
        copy(addresses = addresses, animateToCurrentLocation = true, isLoading = false)
    }

    private fun onAddEditSuccess(snackBarUiState: SnackBarUiState?) {
        updateState {
            copy(
                snackBarUiState = snackBarUiState ?: state.value.snackBarUiState,
            )
        }
        getUserAddresses()

    }

    private fun onErrorOccurred(errorState: ErrorState) {
        onDismissDeleteDialog()
        when (errorState) {
            is ErrorState.IsActiveAddress -> updateState {
                copy(
                    snackBarUiState = SnackBarUiState(
                        snackBarType = SnackBarType.ERROR,
                        isVisible = true,
                        message = Res.string.is_main_address_error
                    ),
                    errorMessage = mapErrorToMessage(errorState)
                )
            }

            else -> updateState {
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
}

