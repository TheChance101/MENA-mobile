package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_activated_successfully
import mena.identity_presentation.generated.resources.address_deleted_successfully
import mena.identity_presentation.generated.resources.error_address_not_found
import mena.identity_presentation.generated.resources.is_main_address_error
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.domain.exception.NoActiveAddressException
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.base.error.handleLocationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapLocationErrorToMessage
import org.jetbrains.compose.resources.StringResource
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

    override fun onAddButtonClicked() {
        navigateToAddressDetails(null, isAdding = true)
        onDismissSnackBar()
    }

    override fun onEditAddressClicked(addressUIState: AddressUIState) {
        navigateToAddressDetails(addressUIState, isAdding = false)
        onDismissSnackBar()
    }

    private fun navigateToAddressDetails(addressUIState: AddressUIState?, isAdding: Boolean) {
        sendNewEffect(
            AddressesScreenUIEffect.NavigateToAddressDetailsScreen(
                addressUIState = addressUIState,
                onSuccess = { snackBar -> onAddEditSuccess(snackBar, isAdding, addressUIState?.id) }
            )
        )
    }

    override fun onClickAddress(addressId: Uuid) {
        val address = findAddressById(addressId)
        if (address?.isMainAddress == false && !address.isActivating) {
            markAddressAsActivating(addressId)
            tryToExecute(
                function = { addressesRepository.setActiveAddress(addressId) },
                onSuccess = { onAddressActivationSuccess() },
                onError = { throwable ->
                    revertAddressActivation(addressId)
                    onAddressOperationError(throwable)
                },
                dispatcher = dispatcher
            )
        }
    }

    override fun onDeleteAddressClicked(addressId: Uuid) = updateState {
        copy(deleteDialogUIState = DeleteDialogUIState(isVisible = true, addressId = addressId))
    }

    override fun onConfirmDeleteAddress() {
        val addressId = state.value.deleteDialogUIState.addressId
        val address = findAddressById(addressId)
        when {
            addressId == null || address == null -> onAddressNotFoundError()
            address.isMainAddress -> onMainAddressDeletionError()
            else -> executeAddressDeletion(addressId)
        }
    }

    override fun onDismissDeleteDialog() = updateState {
        copy(deleteDialogUIState = DeleteDialogUIState(isVisible = false))
    }

    override fun onDismissSnackBar() = updateState {
        copy(snackBarUiState = snackBarUiState.copy(isVisible = false))
    }

    private fun getUserAddresses() {
        tryToExecute(
            function = ::fetchUserAddresses,
            onSuccess = ::onUserAddressesSuccess,
            onError = ::handleUserAddressesError,
            dispatcher = dispatcher
        )
    }

    private fun handleUserAddressesError(throwable: Throwable) {
        val editedId = state.value.editedAddressId
        updateState {
            copy(
                addresses = if (editedId != null) {
                    addresses.map { if (it.id == editedId) it.copy(isRefreshing = false) else it }
                } else addresses,
                isAddingNewAddress = false,
                pendingSnackBar = null,
                editedAddressId = null
            )
        }
        onAddressOperationError(throwable)
    }

    private suspend fun fetchUserAddresses(): List<AddressUIState> {
        val addresses = addressesRepository.getUserAddresses()
        val activeAddress = addressesRepository.getActiveAddress()
        return addresses.map { address ->
            val isActive = activeAddress == address
            val existingAddress = state.value.addresses.find { it.id == address.id }
            address.toUiState(id = address.id, isMainAddress = isActive).copy(
                isDeleting = existingAddress?.isDeleting ?: false,
                isActivating = if (isActive) false else (existingAddress?.isActivating ?: false),
                isRefreshing = false
            )
        }
    }

    private fun onUserAddressesSuccess(addresses: List<AddressUIState>) {
        val pending = state.value.pendingSnackBar
        updateState {
            copy(
                addresses = addresses,
                animateToCurrentLocation = true,
                isLoading = false,
                isAddingNewAddress = false,
                pendingSnackBar = null,
                editedAddressId = null
            )
        }
        // Show snackbar after addresses are updated for synchronized visual feedback
        if (pending != null) {
            updateState {
                copy(snackBarUiState = pending.copy(isVisible = true))
            }
        }
    }

    private fun onAddEditSuccess(snackBarUiState: SnackBarUiState?, isAdding: Boolean, addressId: Uuid?) {
        if (snackBarUiState == null) return
        updateState {
            copy(
                isAddingNewAddress = isAdding,
                pendingSnackBar = snackBarUiState,
                editedAddressId = addressId
            )
        }
        if (!isAdding && addressId != null) {
            markAddressAsRefreshing(addressId)
        }
        getUserAddresses()
    }

    private fun onAddressActivationSuccess() {
        updateState {
            copy(
                pendingSnackBar = buildSnackBar(
                    Res.string.address_activated_successfully,
                    true
                )
            )
        }
        getUserAddresses()
    }

    private suspend fun onAddressDeletionSuccess(addressId: Uuid) {
        delay(300L)
        removeAddress(addressId)
        updateState {
            copy(
                pendingSnackBar = buildSnackBar(
                    Res.string.address_deleted_successfully,
                    true
                )
            )
        }
        getUserAddresses()
    }

    private fun removeAddress(addressId: Uuid) = updateState {
        copy(addresses = addresses.filterNot { it.id == addressId })
    }

    private fun onAddressOperationError(throwable: Throwable) {
        when (throwable) {
            is NoActiveAddressException -> updateState {
                copy(isLoading = false, isAddingNewAddress = false)
            }

            else -> {
                val editedId = state.value.editedAddressId
                updateState {
                    copy(
                        addresses = if (editedId != null) {
                            addresses.map { if (it.id == editedId) it.copy(isRefreshing = false) else it }
                        } else addresses,
                        isAddingNewAddress = false,
                        pendingSnackBar = null,
                        editedAddressId = null
                    )
                }
                showErrorSnackBar(mapErrorMessage(throwable))
            }
        }
    }

    private fun onAddressNotFoundError() {
        val editedId = state.value.editedAddressId
        updateState {
            copy(
                addresses = if (editedId != null) {
                    addresses.map { if (it.id == editedId) it.copy(isRefreshing = false) else it }
                } else addresses,
                isAddingNewAddress = false,
                pendingSnackBar = null,
                editedAddressId = null
            )
        }
        showErrorSnackBar(Res.string.error_address_not_found)
    }

    private fun onMainAddressDeletionError() {
        val editedId = state.value.editedAddressId
        updateState {
            copy(
                addresses = if (editedId != null) {
                    addresses.map { if (it.id == editedId) it.copy(isRefreshing = false) else it }
                } else addresses,
                isAddingNewAddress = false,
                pendingSnackBar = null,
                editedAddressId = null
            )
        }
        showErrorSnackBar(Res.string.is_main_address_error)
    }

    private fun showSuccessSnackBar(message: StringResource) {
        updateState {
            copy(
                snackBarUiState = buildSnackBar(message, true),
                deleteDialogUIState = DeleteDialogUIState(isVisible = false)
            )
        }
    }

    private fun showErrorSnackBar(message: StringResource) {
        updateState {
            copy(
                snackBarUiState = buildSnackBar(message, false),
                deleteDialogUIState = DeleteDialogUIState(isVisible = false)
            )
        }
    }

    private fun buildSnackBar(message: StringResource, isSuccess: Boolean) = SnackBarUiState(
        isVisible = true,
        snackBarType = if (isSuccess) SnackBarType.SUCCESS else SnackBarType.ERROR,
        message = message
    )

    private fun findAddressById(addressId: Uuid?) =
        state.value.addresses.find { it.id == addressId }

    private fun executeAddressDeletion(addressId: Uuid) {
        markAddressAsDeleting(addressId)
        updateState { copy(deleteDialogUIState = DeleteDialogUIState(isVisible = false)) }
        tryToExecute(
            function = { addressesRepository.deleteAddress(addressId) },
            onSuccess = { onAddressDeletionSuccess(addressId) },
            onError = { throwable ->
                revertAddressDeletion(addressId)
                onAddressOperationError(throwable)
            },
            dispatcher = dispatcher
        )
    }

    private fun markAddressAsDeleting(addressId: Uuid) =
        updateAddressState(addressId) { it.copy(isDeleting = true) }

    private fun revertAddressDeletion(addressId: Uuid) =
        updateAddressState(addressId) { it.copy(isDeleting = false) }

    private fun markAddressAsActivating(addressId: Uuid) =
        updateAddressState(addressId) { it.copy(isActivating = true) }

    private fun revertAddressActivation(addressId: Uuid) =
        updateAddressState(addressId) { it.copy(isActivating = false) }

    private fun markAddressAsRefreshing(addressId: Uuid) =
        updateAddressState(addressId) { it.copy(isRefreshing = true) }

    private fun updateAddressState(addressId: Uuid, update: (AddressUIState) -> AddressUIState) {
        updateState {
            copy(
                addresses = addresses.map { if (it.id == addressId) update(it) else it }
            )
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is LocationException -> mapLocationErrorToMessage(handleLocationException(throwable))
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleAuthenticationException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}