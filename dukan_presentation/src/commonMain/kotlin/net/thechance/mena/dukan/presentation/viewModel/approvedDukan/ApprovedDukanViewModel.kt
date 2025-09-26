package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import kotlinx.coroutines.CoroutineDispatcher
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState

class ApprovedDukanViewModel(
    private val shelfRepository: ShelfRepository,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<ApprovedDukanUiState, ApprovedDukanEffect>(ApprovedDukanUiState()), ApprovedDukanInteractionListener {

    init {
        loadShelves()
    }

    override fun onBackButtonClicked() {
        emitEffect(ApprovedDukanEffect.NavigateBack)
    }

    override fun onDismissSnackBar() {
        updateState { copy(showSnackBar = false) }
    }

    override fun onCategorySelected(categoryId: String) {
        val category = state.value.categories.find { it.id == categoryId }
        if (category != null) {
            val updatedSelectedCategories = state.value.selectedCategories.toMutableSet()
            if (updatedSelectedCategories.contains(category)) {
                updatedSelectedCategories.remove(category)
            } else {
                updatedSelectedCategories.add(category)
            }
            updateState { copy(selectedCategories = updatedSelectedCategories) }
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

    // SelectionRow methods for shelves
    override fun isShelfSelected(): (DukanCategoryUiState) -> Boolean = { shelf ->
        state.value.selectedCategories.contains(shelf)
    }

    override fun onShelfSelected(shelf: DukanCategoryUiState): Boolean {
        val updatedSelectedCategories = state.value.selectedCategories.toMutableSet()
        updatedSelectedCategories.add(shelf)
        updateState { copy(selectedCategories = updatedSelectedCategories) }
        return true
    }

    override fun onShelfDeselected(shelf: DukanCategoryUiState): Boolean {
        val updatedSelectedCategories = state.value.selectedCategories.toMutableSet()
        updatedSelectedCategories.remove(shelf)
        updateState { copy(selectedCategories = updatedSelectedCategories) }
        return true
    }

    override fun onShelfEnabled(shelf: DukanCategoryUiState): Boolean = true

    private fun loadShelves() {
        tryToExecute(
            onStart = { updateState { copy(isLoading = true) } },
            block = { shelfRepository.getMyDukanShelves() },
            onSuccess = { shelves ->
                updateState {
                    copy(
                        shelves = shelves,
                        productCount = shelves.size,
                        isLoading = false
                    )
                }
            },
            onError = {
                updateState { copy(isLoading = false) }
                showSnackBar("Failed to load shelves")
            },
            dispatcher = ioDispatcher
        )
    }

    private fun showSnackBar(message: String) {
        updateState { copy(showSnackBar = true) }
    }
}