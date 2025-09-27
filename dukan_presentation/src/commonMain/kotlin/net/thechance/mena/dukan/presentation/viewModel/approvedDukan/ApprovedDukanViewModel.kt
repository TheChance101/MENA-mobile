package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ApprovedDukanViewModel(
    private val shelfRepository: ShelfRepository,
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

    override fun onDismissSnackBar() {
        updateState {
            copy(snackBarMessage = null)
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
        state.value.selectedShelves.contains(shelf)
    }

    override fun onShelfSelected(shelf: Shelf): Boolean {
        val updatedSelectedShelves = state.value.selectedShelves.toMutableSet()
        updatedSelectedShelves.add(shelf)
        updateState { copy(selectedShelves = updatedSelectedShelves) }
        loadProductsForSelectedShelves()
        return true
    }

    override fun onShelfDeselected(shelf: Shelf): Boolean {
        val updatedSelectedShelves = state.value.selectedShelves.toMutableSet()
        updatedSelectedShelves.remove(shelf)
        updateState { copy(selectedShelves = updatedSelectedShelves) }
        loadProductsForSelectedShelves()
        return true
    }

    override fun onShelfEnabled(shelf: Shelf): Boolean = true

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
                selectedShelves = selectFirstShelfByDefault(shelves),
                isLoading = false
            )
        }
        loadProductsForSelectedShelves()
    }

    private fun loadProductsForSelectedShelves() {
        val selectedShelves = state.value.selectedShelves
        when {
            selectedShelves.isNotEmpty() -> loadProductsFromRepository(selectedShelves)
            else -> clearProducts()
        }
    }

    private fun loadProductsFromRepository(selectedShelves: Set<Shelf>) {
        tryToExecute(
            onStart = { updateState { copy(isLoadingProducts = isLoading) } },
            block = { getProductsForShelves(selectedShelves) },
            onSuccess = { products -> handleProductsLoaded(products) },
            onError = { updateState { copy(isLoadingProducts = false) } }
        )
    }

    private suspend fun getProductsForShelves(selectedShelves: Set<Shelf>): List<Product> {
        return selectedShelves.flatMap { shelf ->
            productRepository.getProductsByShelfId(shelf.id)
        }
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

    private fun selectFirstShelfByDefault(shelves: List<Shelf>): Set<Shelf> {
        return if (shelves.isNotEmpty()) setOf(shelves.first()) else emptySet()
    }

}
