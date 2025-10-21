package net.thechance.mena.dukan.presentation.viewModel.manageDukan

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
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
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.pagination.base.createPagingSource
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class ManageDukanViewModel(
    private val shelfRepository: ShelfRepository,
    private val productRepository: ProductRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ManageDukanUiState, ManageDukanEffect>(
    initialState = ManageDukanUiState(),
    defaultDispatcher = defaultDispatcher
), ManageDukanInteractionListener {

    init {
        loadShelves()
    }

    override fun onBackButtonClicked() {
        emitEffect(ManageDukanEffect.NavigateBack)
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
        emitEffect(ManageDukanEffect.NavigateToAddProduct)
    }

    override fun onEditShelfClicked() {
        emitEffect(
            ManageDukanEffect.NavigateToManageShelf(
                shelfId = state.value.selectedShelf?.id.orEmpty(),
                shelfTitle = state.value.selectedShelf?.name.orEmpty()
            )
        )
    }

    override fun onAddShelfClicked() {
        emitEffect(ManageDukanEffect.NavigateToAddShelf)
    }

    override fun onProductClick(product: ManageDukanUiState.ProductUiState) {
        emitEffect(ManageDukanEffect.NavigateToProductDetails)
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

    override fun onShelfAddedSuccessfully() {
        onShelfAdded(message = Res.string.add_shelf_successfully, type = SnackBarType.SUCCESS)
        loadShelves()
    }

    private fun loadShelves() {


        tryToExecute(
            onStart = ::onLoadShelvesStart,
            block = shelfRepository::getMyDukanShelves,
            onSuccess = ::handleShelvesLoaded,
            onError = { handleLoadShelvesError() }
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

    private fun handleLoadShelvesError() {
        updateState {
            copy(
                shelvesState = ManageDukanUiState.ShelvesState.EMPTY,
                productState = ManageDukanUiState.ProductsState.EMPTY,
                products = PagingData()
            )
        }
    }

    private fun handleShelvesLoaded(shelves: List<Shelf>) {
        val shelvesState = if (shelves.isEmpty())
            ManageDukanUiState.ShelvesState.EMPTY
        else
            ManageDukanUiState.ShelvesState.LOADED
        updateState {
            copy(
                shelves = shelves.map(Shelf::toUiState),
                selectedShelf = shelves.firstOrNull()?.toUiState(),
                shelvesState = shelvesState
            )
        }
        loadProductsForSelectedShelf()
    }

    private fun loadProductsForSelectedShelf() {
        val selectedShelf = state.value.selectedShelf
        selectedShelf?.let { shelf ->
            viewModelScope.launch {
                pager.refresh()
            }
            loadProductsFromRepository()
            viewModelScope.launch {
                pager.refresh()
            }
        }
    }

    private fun loadProductsFromRepository() {
        tryToCollect(
            onStart = ::onLoadProductsFromRepositoryStart,
            block = { pager.flow },
            onCollect = ::onProductsLoaded,
        )
        viewModelScope.launch {
            pager.load()
        }
    }

    private fun onLoadProductsFromRepositoryStart() {
        updateState {
            copy(
                productState = ManageDukanUiState.ProductsState.LOADING,
                products = PagingData()
            )
        }
    }

    override fun onDismissDeleteShelfConfirmationDialog() {
        val dialogState = state.value.deleteShelfConfirmationDialogUiState
        updateState {
            copy(
                showDeleteConfirmationDialog = false,
                deleteShelfConfirmationDialogUiState = dialogState?.copy(isDialogVisible = false)
            )
        }
    }

    override fun onShowDeleteShelfDailog(
        shelfId: String
    ) {
        val hasProducts = state.value.products.items.isNotEmpty()
        updateState {
            copy(
                deleteShelfConfirmationDialogUiState = ManageDukanUiState.DeleteShelfConfirmationDialogUiState(
                    title = updateDialogTitle(hasProducts),
                    description = updateDialogDescription(hasProducts),
                    type = updateDialogType(hasProducts),
                    shelfId = shelfId,
                    isDialogVisible = true
                ),
                showDeleteConfirmationDialog = true,
            )
        }
    }

    private fun updateDialogTitle(hasProducts: Boolean): StringResource {
        return if (!hasProducts) Res.string.delete_shelf_title else Res.string.dismiss_title
    }

    private fun updateDialogDescription(hasProducts: Boolean): StringResource {
        return if (!hasProducts) Res.string.delete_shelf_description else Res.string.dismiss_description
    }

    private fun updateDialogType(hasProducts: Boolean): ManageDukanUiState.ConfirmDialogType {
        return if (hasProducts.not())
            ManageDukanUiState.ConfirmDialogType.DELETE
        else
            ManageDukanUiState.ConfirmDialogType.DISMISS
    }

    override fun onDeleteConfirmed(shelfId: String) {
        tryToExecute(
            block = { shelfRepository.deleteShelf(shelfId) },
            onSuccess = ::onDeleteShelfSuccess,
            onError = ::onDeleteShelfError
        )
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
}
