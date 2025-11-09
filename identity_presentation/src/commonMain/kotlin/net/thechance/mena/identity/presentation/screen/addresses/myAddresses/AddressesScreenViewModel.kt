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
import net.thechance.mena.identity.presentation.mapper.mapToUIState
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
        navigateToAddressDetails(isAdding = true, addressUIState = null)
    }

    override fun onEditAddressClicked(addressUIState: AddressUIState) {
        navigateToAddressDetails(isAdding = false, addressUIState = addressUIState)
    }

    private fun navigateToAddressDetails(isAdding: Boolean, addressUIState: AddressUIState?) {
        sendNewEffect(
            AddressesScreenUIEffect.NavigateToAddressDetailsScreen(
                addressUIState = addressUIState,
                onSuccess = { snackBar -> onAddEditSuccess(snackBar, isAdding) }
            )
        )
        onDismissSnackBar()
    }

    override fun onClickAddress(addressId: Uuid) {
        val address = findAddressById(addressId)
        if (address?.isMainAddress == false && !address.isActivating) {
            activateAddress(addressId)
        }
    }

    private fun activateAddress(addressId: Uuid) {
        tryToExecute(
            function = { addressesRepository.setActiveAddress(addressId) },
            onSuccess = { onAddressActivationSuccess() },
            onError = ::onAddressOperationError,
            dispatcher = dispatcher
        )
    }

    override fun onDeleteAddressClicked(addressId: Uuid) = updateState {
        copy(deleteDialogUIState = DeleteDialogUIState(isVisible = true, addressId = addressId))
    }

    override fun onConfirmDeleteAddress() {
        val addressId = state.value.deleteDialogUIState.addressId
        val address = findAddressById(addressId)
        handleAddressDeletionValidation(addressId, address)
    }

    private fun handleAddressDeletionValidation(addressId: Uuid?, address: AddressUIState?) {
        when {
            addressId == null -> onAddressNotFoundError()
            address == null -> onAddressNotFoundError()
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
        resetLoadingState()
        onAddressOperationError(throwable)
    }

    private fun resetLoadingState() = updateState {
        copy(isRefreshing = false, isAddingNewAddress = false, pendingSnackBar = null)
    }

    private suspend fun fetchUserAddresses(): List<AddressUIState> {
        val addresses = addressesRepository.getUserAddresses()
        val activeAddress = addressesRepository.getActiveAddress()
        val existingAddresses = state.value.addresses
        return addresses.map { address ->
            address.mapToUIState(activeAddress, existingAddresses)
        }
    }

    private fun onUserAddressesSuccess(addresses: List<AddressUIState>) = updateState {
        val pendingSnackBar = pendingSnackBar
        copy(
            addresses = addresses,
            animateToCurrentLocation = true,
            isLoading = false,
            isRefreshing = false,
            isAddingNewAddress = false,
            pendingSnackBar = null,
            snackBarUiState = pendingSnackBar ?: snackBarUiState
        )
    }

    private fun onAddEditSuccess(snackBarUiState: SnackBarUiState?, isAdding: Boolean) {
        updateState {
            copy(
                isRefreshing = true,
                isAddingNewAddress = isAdding,
                pendingSnackBar = snackBarUiState
            )
        }
        getUserAddresses()
    }

    private fun onAddressActivationSuccess() {
        showPendingSuccessSnackBar(Res.string.address_activated_successfully)
        getUserAddresses()
    }

    private fun showPendingSuccessSnackBar(message: StringResource) = updateState {
        copy(
            pendingSnackBar = SnackBarUiState(
                isVisible = true,
                snackBarType = SnackBarType.SUCCESS,
                message = message
            )
        )
    }

    private suspend fun onAddressDeletionSuccess(addressId: Uuid) {
        delay(300L)
        removeAddressFromList(addressId)
        showPendingSuccessSnackBar(Res.string.address_deleted_successfully)
        getUserAddresses()
    }

    private fun removeAddressFromList(addressId: Uuid) = updateState {
        val updatedAddresses = addresses.filter { it.id != addressId }
        copy(addresses = updatedAddresses)
    }

    private fun onAddressOperationError(throwable: Throwable) {
        when (throwable) {
            is NoActiveAddressException -> stopLoading()
            else -> handleGenericAddressError(throwable)
        }
    }

    private fun stopLoading() = updateState {
        copy(isLoading = false, isRefreshing = false, isAddingNewAddress = false)
    }

    private fun handleGenericAddressError(throwable: Throwable) {
        resetLoadingState()
        showErrorSnackBar(mapErrorMessage(throwable))
    }

    private fun onAddressNotFoundError() {
        resetLoadingState()
        showErrorSnackBar(Res.string.error_address_not_found)
    }

    private fun onMainAddressDeletionError() {
        resetLoadingState()
        showErrorSnackBar(Res.string.is_main_address_error)
    }

    private fun showSuccessSnackBar(message: StringResource) = updateState {
        copy(
            snackBarUiState = SnackBarUiState(
                isVisible = true,
                snackBarType = SnackBarType.SUCCESS,
                message = message
            ),
            deleteDialogUIState = DeleteDialogUIState(isVisible = false)
        )
    }

    private fun showErrorSnackBar(message: StringResource) = updateState {
        copy(
            snackBarUiState = SnackBarUiState(
                isVisible = true,
                snackBarType = SnackBarType.ERROR,
                message = message
            ),
            deleteDialogUIState = DeleteDialogUIState(isVisible = false)
        )
    }

    private fun findAddressById(addressId: Uuid?): AddressUIState? {
        return state.value.addresses.find { it.id == addressId }
    }

    private fun executeAddressDeletion(addressId: Uuid) {
        markAddressAsDeleting(addressId)
        closeDeleteDialog()
        deleteAddressFromRepo(addressId)
    }

    private fun closeDeleteDialog() = updateState {
        copy(deleteDialogUIState = DeleteDialogUIState(isVisible = false))
    }

    private fun deleteAddressFromRepo(addressId: Uuid) {
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

    private fun markAddressAsDeleting(addressId: Uuid) = updateState {
        val updatedAddresses = addresses.map {
            if (it.id == addressId) it.copy(isDeleting = true) else it
        }
        copy(addresses = updatedAddresses)
    }

    private fun revertAddressDeletion(addressId: Uuid) = updateState {
        val updatedAddresses = addresses.map {
            if (it.id == addressId) it.copy(isDeleting = false) else it
        }
        copy(
            addresses = updatedAddresses,
            isRefreshing = false,
            isAddingNewAddress = false,
            pendingSnackBar = null
        )
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