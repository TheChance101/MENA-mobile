package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.address_activated_successfully
import mena.identity_presentation.generated.resources.address_deleted_successfully
import mena.identity_presentation.generated.resources.error_address_not_found
import mena.identity_presentation.generated.resources.is_main_address_error
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.LocationException
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

    override fun onAddButtonClicked() = sendNewEffect(
        AddressesScreenUIEffect.NavigateToAddressDetailsScreen(
            addressUIState = null,
            onSuccess = ::onAddEditSuccess
        )
    )

    override fun onEditAddressClicked(addressUIState: AddressUIState) =
        sendNewEffect(
            AddressesScreenUIEffect.NavigateToAddressDetailsScreen(
                addressUIState = addressUIState,
                onSuccess = ::onAddEditSuccess
            )
        )

    override fun onClickAddress(addressId: Uuid) {
        val address = findAddressById(addressId)

        if (address?.isMainAddress == false) {
            tryToExecute(
                function = { addressesRepository.setActiveAddress(addressId) },
                onSuccess = { onAddressActivationSuccess() },
                onError = ::onAddressOperationError,
                dispatcher = dispatcher
            )
        }
    }

    override fun onDeleteAddressClicked(addressId: Uuid) = updateState {
        copy(
            deleteDialogUIState = DeleteDialogUIState(
                isVisible = true, addressId = addressId
            ),
        )
    }

    override fun onConfirmDeleteAddress() {
        val addressId = state.value.deleteDialogUIState.addressId
        val address = findAddressById(addressId)

        when {
            addressId == null -> onAddressNotFoundError()
            address == null -> onAddressNotFoundError()
            address.isMainAddress -> onMainAddressDeletionError()
            else -> executeAddressDeletion(addressId)
        }
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
            function = ::fetchUserAddresses,
            onSuccess = ::onUserAddressesSuccess,
            onError = ::onAddressOperationError,
            dispatcher = dispatcher
        )
    }

    private suspend fun fetchUserAddresses(): List<AddressUIState> {
        val addresses = addressesRepository.getUserAddresses()
        val activeAddress = addressesRepository.getActiveAddress()
        return addresses.map { address ->
            val isActive = activeAddress == address
            address.toUiState(id = address.id, isMainAddress = isActive)
        }
    }

    private fun onUserAddressesSuccess(addresses: List<AddressUIState>) = updateState {
        copy(addresses = addresses, animateToCurrentLocation = true, isLoading = false)
    }

    private fun onAddEditSuccess(snackBarUiState: SnackBarUiState?) {
        updateState {
            copy(snackBarUiState = snackBarUiState ?: state.value.snackBarUiState)
        }
        getUserAddresses()
    }

    private fun onAddressActivationSuccess() {
        getUserAddresses()
        showSuccessSnackBar(Res.string.address_activated_successfully)
    }

    private fun onAddressDeletionSuccess() {
        getUserAddresses()
        showSuccessSnackBar(Res.string.address_deleted_successfully)
    }

    private fun onAddressOperationError(throwable: Throwable) {
        dismissDeleteDialog()
        showErrorSnackBar(mapErrorMessage(throwable))
    }

    private fun onAddressNotFoundError() {
        showErrorSnackBar(Res.string.error_address_not_found)
    }

    private fun onMainAddressDeletionError() {
        showErrorSnackBar(Res.string.is_main_address_error)
    }

    private fun showSuccessSnackBar(message: StringResource) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    isVisible = true,
                    snackBarType = SnackBarType.SUCCESS,
                    message = message
                ),
                deleteDialogUIState = DeleteDialogUIState(isVisible = false)
            )
        }
    }

    private fun showErrorSnackBar(message: StringResource) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    isVisible = true,
                    snackBarType = SnackBarType.ERROR,
                    message = message
                ),
                deleteDialogUIState = DeleteDialogUIState(isVisible = false)
            )
        }
    }

    private fun dismissDeleteDialog() = updateState {
        copy(deleteDialogUIState = DeleteDialogUIState(isVisible = false))
    }

    private fun findAddressById(addressId: Uuid?): AddressUIState? {
        return state.value.addresses.find { it.id == addressId }
    }

    private fun executeAddressDeletion(addressId: Uuid) {
        tryToExecute(
            function = { addressesRepository.deleteAddress(addressId) },
            onSuccess = { onAddressDeletionSuccess() },
            onError = ::onAddressOperationError,
            dispatcher = dispatcher
        )
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is LocationException -> mapLocationErrorToMessage(handleLocationException(throwable))
            is AuthenticationException -> mapAuthenticationErrorToMessage(handleAuthenticationException(throwable))
            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}