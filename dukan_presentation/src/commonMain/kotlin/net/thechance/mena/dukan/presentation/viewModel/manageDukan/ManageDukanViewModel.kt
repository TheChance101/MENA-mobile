package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.DeletionNotAllowedException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.pagination.base.createPagingSource
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.DeleteDialogState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.DialogType
import org.jetbrains.compose.resources.StringResource

class ManageDukanViewModel(
    private val shelfRepository: ShelfRepository,
    private val productRepository: ProductRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageDukanUiState, ManageDukanUiEffect>(
    initialState = ManageDukanUiState(),
    defaultDispatcher = defaultDispatcher
), ManageDukanInteractionListener {

    init {
        loadShelves()
        collectProducts()
    }

    override fun onBackButtonClicked() {
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

    override fun onProductClick(product: ManageDukanUiState.ProductUiState) {
        emitEffect(ManageDukanUiEffect.NavigateToProductDetails)
    }

    override fun isShelfSelected(shelf: ManageDukanUiState.ShelfUiState): Boolean {
        return state.value.selectedShelf == shelf
    }

    override fun onShelfSelected(shelf: ManageDukanUiState.ShelfUiState) {
        if (state.value.selectedShelf != shelf) {
            updateState {
                copy(
                    selectedShelf = shelf,
                    productState = ManageDukanUiState.ProductsState.LOADING,
                    products = PagingData()
                )
            }
            loadProductsForSelectedShelf()
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
        val hasProducts = state.value.products.items.isNotEmpty()
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

    override fun onDeleteConfirmed(shelfId: String) {
        tryToExecute(
            block = { shelfRepository.deleteShelf(shelfId) },
            onSuccess = ::onDeleteShelfSuccess,
            onError = ::onDeleteShelfError
        )
    }

    private fun loadShelves() {
        tryToExecute(
            onStart = ::onLoadShelvesStart,
            block = shelfRepository::getMyDukanShelves,
            onSuccess = ::onLoadShelvesSuccess,
            onError = ::handleLoadShelvesError
        )
    }

    private fun onLoadShelvesStart() {
        updateState {
            copy(
                shelvesState = ManageDukanUiState.ShelvesState.LOADING,
                productState = ManageDukanUiState.ProductsState.LOADING,
                products = PagingData()
            )
        }
    }

    private fun onLoadShelvesSuccess(shelves: List<Shelf>) {
        val newSelectedShelf = updateShelvesState(shelves)
        if (newSelectedShelf != null) {
            loadProductsForSelectedShelf()
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
        return newSelectedShelf
    }

    private fun handleLoadShelvesError(throwable: Throwable) {
        updateState {
            copy(
                shelvesState = ManageDukanUiState.ShelvesState.EMPTY,
                productState = ManageDukanUiState.ProductsState.EMPTY,
                products = PagingData()
            )
        }
    }

    private fun loadProductsForSelectedShelf() {
        viewModelScope.launch {
            pager.refresh()
        }
    }

    private fun collectProducts() {
        tryToCollect(
            onStart = ::onLoadProducts,
            block = { pager.flow },
            onCollect = ::onProductsLoaded,
        )
        viewModelScope.launch {
            pager.load()
        }
    }

    private fun onLoadProducts() {
        updateState {
            copy(
                productState = ManageDukanUiState.ProductsState.LOADING,
                products = PagingData()
            )
        }
    }

    private fun onProductsLoaded(products: PagingData<ManageDukanUiState.ProductUiState>) {
        val productState = when {
            products.isLoading && products.items.isEmpty() -> ManageDukanUiState.ProductsState.LOADING
            products.items.isEmpty() -> ManageDukanUiState.ProductsState.EMPTY
            else -> ManageDukanUiState.ProductsState.LOADED
        }
        updateState {
            copy(
                productState = productState,
                products = products
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

    val pager = createPagingSource(
        mapper = { it.toUiState() },
    ) {
        productRepository.getProductsByShelfId(
            shelfId = state.value.selectedShelf?.id.orEmpty(),
            page = it,
            size = 20
        ).also { result ->
            updateState {
                copy(
                    totalProducts = result.totalItems
                )
            }
        }
    }
}