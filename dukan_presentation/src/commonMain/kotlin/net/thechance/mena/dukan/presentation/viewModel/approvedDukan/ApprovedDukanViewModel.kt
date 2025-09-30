package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.delete_shelf_description
import mena.dukan_presentation.generated.resources.delete_shelf_success
import mena.dukan_presentation.generated.resources.delete_shelf_title
import mena.dukan_presentation.generated.resources.dismiss_description
import mena.dukan_presentation.generated.resources.dismiss_title
import mena.dukan_presentation.generated.resources.error_for_delete_shelf
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.CreateShelfRepository
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class ApprovedDukanViewModel(
    private val shelfRepository: CreateShelfRepository,
    private val productRepository: ProductRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ApprovedDukanUiState, ApprovedDukanEffect>(
    initialState = ApprovedDukanUiState(),
    defaultDispatcher = defaultDispatcher
), ApprovedDukanInteractionListener {

    init {
        loadShelves()
    }

    override fun onBackButtonClicked() {
        emitEffect(ApprovedDukanEffect.NavigateBack)
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

    override fun onDismissSnackBar() {
        updateState {
            copy(snackBarState = null)
        }
    }

    override fun onAddProductClicked() {
        emitEffect(ApprovedDukanEffect.NavigateToAddProduct)
    }

    override fun onEditShelfClicked() {
        emitEffect(ApprovedDukanEffect.NavigateToEditShelf)
    }

    override fun onAddShelfClicked() {
        emitEffect(ApprovedDukanEffect.NavigateToAddShelf)
    }

    override fun onProductClick(product: Product) {
        emitEffect(ApprovedDukanEffect.NavigateToProductDetails)
    }

    override fun isShelfSelected(): (Shelf) -> Boolean = { shelf ->
        state.value.selectedShelf == shelf
    }

    override fun onShelfSelected(shelf: Shelf): Boolean {
        if (state.value.selectedShelf == shelf) return true
        updateState { copy(selectedShelf = shelf) }
        loadProductsForSelectedShelf()
        return true
    }

    override fun onShelfDeselected(shelf: Shelf): Boolean {
        if (state.value.selectedShelf != shelf) return true
        updateState { copy(selectedShelf = null) }
        loadProductsForSelectedShelf()
        return true
    }

    override fun onShelfEnabled(shelf: Shelf): Boolean = true

    override fun onShelfAddedSuccessfully() {
        showSnackBar(message = Res.string.add_shelf_successfully, type = SnackBarType.SUCCESS)
        loadShelves()
    }

    private fun loadShelves() {
        tryToExecute(
            onStart = { updateState { copy(isLoading = true) } },
            block = { shelfRepository.getMyDukanShelves() },
            onSuccess = { shelves -> handleShelvesLoaded(shelves) },
            onError = { handleLoadShelvesError() }
        )
    }

    private fun handleLoadShelvesError() {
        updateState {
            copy(isLoading = false)
        }
    }

    private fun handleShelvesLoaded(shelves: List<Shelf>) {
        updateState {
            copy(
                shelves = shelves,
                availableShelves = shelves,
                selectedShelf = selectFirstShelfByDefault(shelves),
                isLoading = false
            )
        }
        loadProductsForSelectedShelf()
    }

    private fun loadProductsForSelectedShelf() {
        val selectedShelf = state.value.selectedShelf
        if (selectedShelf != null) loadProductsFromRepository(selectedShelf) else clearProducts()
    }

    private fun loadProductsFromRepository(selectedShelf: Shelf) {
        tryToExecute(
            onStart = { updateState { copy(isLoadingProducts = isLoading) } },
            block = { productRepository.getProductsByShelfId(selectedShelf.id) },
            onSuccess = { products -> handleProductsLoaded(products) },
            onError = { updateState { copy(isLoadingProducts = false) } }
        )
    }

    override fun onDismissDeleteShelfConfirmationDialog() {
        updateState {
            copy(showDeleteConfirmationDialog = false)
        }
    }

    override fun onShowDeleteShelfConfirmationDialog() {
        val hasProducts = state.value.products.isNotEmpty()
        updateState {
            copy(
                deleteShelfConfirmationDialogUiState = DeleteShelfConfirmationDialogUiState(
                    title = updateDialogTitle(hasProducts),
                    description = updateDialogDescription(hasProducts) ,
                    type = updateDialogType(hasProducts)
                ),
                showDeleteConfirmationDialog = true
            )
        }
    }

    private fun updateDialogTitle(hasProducts: Boolean): StringResource{
        return if (!hasProducts) Res.string.delete_shelf_title else Res.string.dismiss_title
    }
    private fun updateDialogDescription(hasProducts: Boolean): StringResource{
        return if (!hasProducts) Res.string.delete_shelf_description else Res.string.dismiss_description
    }
    private fun updateDialogType(hasProducts: Boolean): ConfirmDialogType{
        return if (!hasProducts) ConfirmDialogType.DELETE else ConfirmDialogType.DISMISS
    }
    override fun deleteShelf(shelfId: String) {
        tryToExecute(
            block = { shelfRepository.deleteShelf(shelfId) },
            onSuccess = { deleteShelfSuccess() },
            onError = ::deleteShelfFail
        )
    }

    private fun deleteShelfSuccess() {
        onDismissDeleteShelfConfirmationDialog()
        showSnackBar(type = SnackBarType.SUCCESS, message = Res.string.delete_shelf_success)
    }

    private fun deleteShelfFail(error: Throwable) {
        onDismissDeleteShelfConfirmationDialog()
        showSnackBar(type = SnackBarType.ERROR, message = Res.string.error_for_delete_shelf)
    }

    private fun handleProductsLoaded(products: List<Product>) {
        updateState {
            copy(
                products = products,
                totalProducts = products.size,
                isLoadingProducts = false
            )
        }
    }

    private fun clearProducts() {
        updateState {
            copy(
                products = emptyList(),
                totalProducts = 0,
                isLoadingProducts = false
            )
        }
    }

    private fun selectFirstShelfByDefault(shelves: List<Shelf>): Shelf? {
        return shelves.firstOrNull()
    }

}
