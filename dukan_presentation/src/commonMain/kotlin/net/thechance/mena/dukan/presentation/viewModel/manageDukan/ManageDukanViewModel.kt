package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete_shelf_description
import mena.dukan_presentation.generated.resources.delete_shelf_success
import mena.dukan_presentation.generated.resources.delete_shelf_title
import mena.dukan_presentation.generated.resources.dismiss_description
import mena.dukan_presentation.generated.resources.dismiss_title
import mena.dukan_presentation.generated.resources.error_for_delete_shelf
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.no_internet_message
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.DeletionNotAllowedException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.DeleteDialogState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.DialogType
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ManageDukanViewModel(
    private val shelfRepository: ShelfRepository,
    private val productRepository: ProductRepository,
    private val dukanRepository: DukanManagementRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageDukanUiState, ManageDukanUiEffect>(
    initialState = ManageDukanUiState(),
    defaultDispatcher = defaultDispatcher
), ManageDukanInteractionListener {

    init {
        getDukanActivationStatus()
    }

    override fun onBackClicked() {
        emitEffect(ManageDukanUiEffect.NavigateBack)
    }

    override fun onShelfAdded(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    snackBarType = type,
                    message = message
                )
            )
        }
        loadShelves()
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(snackBarState = null)
        }
    }

    override fun onAddProductClicked() {
        emitEffect(ManageDukanUiEffect.NavigateToAddProduct)
    }

    override fun onEditShelfClicked() {
        emitEffect(
            ManageDukanUiEffect.NavigateToManageShelf(
                shelfId = state.value.selectedShelf?.id.orEmpty(),
                shelfTitle = state.value.selectedShelf?.name.orEmpty()
            )
        )
    }

    override fun onAddShelfClicked() {
        emitEffect(ManageDukanUiEffect.NavigateToAddShelf)
    }

    override fun onProductClicked(product: ManageDukanUiState.ProductUiState) {
        emitEffect(ManageDukanUiEffect.NavigateToProductDetails)
    }

    override fun onEditProductClicked(productId: String) {
        emitEffect(
            ManageDukanUiEffect.NavigateToEditProduct(
                productId = productId
            )
        )
    }

    override fun isShelfSelected(shelf: ManageDukanUiState.ShelfUiState): Boolean {
        return state.value.selectedShelf == shelf
    }

    override fun onShelfSelected(shelf: ManageDukanUiState.ShelfUiState) {
        if (state.value.selectedShelf != shelf) {
            updateState {
                copy(
                    selectedShelf = shelf
                )
            }
            collectProducts()
        }
    }

    override fun onDismissDeleteShelfConfirmationDialog() {
        updateState {
            copy(deleteDialog = null)
        }
    }

    override fun onShowDeleteShelfDialog(
        shelfId: String
    ) {
        viewModelScope.launch(defaultDispatcher) {
            val hasProducts = state.value.totalProducts > 0
            updateState {
                copy(
                    deleteDialog = DeleteDialogState(
                        title = updateDialogTitle(hasProducts),
                        description = updateDialogDescription(hasProducts),
                        type = updateDialogType(hasProducts),
                        shelfId = shelfId,
                    )
                )
            }
        }
    }

    override fun onDeleteConfirmed(shelfId: String) {
        tryToExecute(
            block = { shelfRepository.deleteShelf(shelfId) },
            onSuccess = ::onDeleteShelfSuccess,
            onError = ::onDeleteShelfError
        )
    }

    fun getDukanActivationStatus() {
        tryToExecute(
            block = dukanRepository::getDukanActivationStatus,
            onSuccess = ::updateActivationStatus,
        )
    }

    private fun updateActivationStatus(status: Dukan.Activation) {
        updateState {
            copy(activation = status.toUiState())
        }
        loadShelves()
    }

    private fun loadShelves() {
        tryToExecute(
            onStart = ::onLoadShelvesStart,
            block = shelfRepository::getMyDukanShelves,
            onSuccess = ::updateShelvesState,
            onError = ::handleLoadShelvesError
        )
    }

    private fun onLoadShelvesStart() {
        updateState {
            copy(
                shelvesState = ManageDukanUiState.ShelvesState.LOADING,
            )
        }
    }

    private fun updateShelvesState(shelves: List<Shelf>): ManageDukanUiState.ShelfUiState? {
        val shelvesState = if (shelves.isEmpty())
            ManageDukanUiState.ShelvesState.EMPTY
        else
            ManageDukanUiState.ShelvesState.LOADED

        val newSelectedShelf = shelves.firstOrNull()?.toUiState()

        updateState {
            copy(
                shelves = shelves.map(Shelf::toUiState),
                selectedShelf = newSelectedShelf,
                shelvesState = shelvesState
            )
        }
        collectProducts()
        return newSelectedShelf
    }

    private fun handleLoadShelvesError(throwable: Throwable) {
        updateState {
            copy(
                shelvesState = ManageDukanUiState.ShelvesState.EMPTY,
            )
        }
    }


    private fun collectProducts() {
        tryToCollect(
            block = ::createPagingSourceData,
            onCollect = ::onProductsLoaded,
        )
    }

    private fun createPagingSourceData(): Flow<PagingData<ManageDukanUiState.ProductUiState>> {
        return createPagingSourceFlow(
            mapper = { it.toUiState() },
        ) { pageNumber, pageSize ->
            productRepository.getProductsByShelfId(
                shelfId = state.value.selectedShelf?.id.orEmpty(),
                page = pageNumber,
                size = pageSize
            ).also { result -> setTotalProducts(result.totalItems) }.items
        }
    }

    private fun setTotalProducts(totalItems: Long) {
        updateState {
            copy(totalProducts = totalItems)
        }
    }

    private fun onProductsLoaded(products: PagingData<ManageDukanUiState.ProductUiState>) {
        updateState {
            copy(
                products = flowOf(products)
            )
        }
    }

    private fun updateDialogTitle(hasProducts: Boolean): StringResource {
        return if (!hasProducts) Res.string.delete_shelf_title else Res.string.dismiss_title
    }

    private fun updateDialogDescription(hasProducts: Boolean): StringResource {
        return if (!hasProducts) Res.string.delete_shelf_description else Res.string.dismiss_description
    }

    private fun updateDialogType(hasProducts: Boolean): DialogType {
        return if (hasProducts.not())
            DialogType.DELETE
        else
            DialogType.DISMISS
    }

    private fun onDeleteShelfSuccess(unit: Unit) {
        onDismissDeleteShelfConfirmationDialog()
        loadShelves()
        showSnackBar(type = SnackBarType.SUCCESS, message = Res.string.delete_shelf_success)
    }

    private fun onDeleteShelfError(throwable: Throwable) {
        onDismissDeleteShelfConfirmationDialog()
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_message
            is DeletionNotAllowedException -> Res.string.error_for_delete_shelf
            else -> Res.string.error_general
        }
        showSnackBar(type = SnackBarType.ERROR, message = messageRes)
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    snackBarType = type,
                    message = message
                )
            )
        }
    }

    fun onEditShelfName(message: StringResource, snackBarType: SnackBarType) {
        showSnackBar(message, snackBarType)
        loadShelves()
    }
}