package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import kotlinx.coroutines.CoroutineDispatcher
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ApprovedDukanViewModel(
    private val shelfRepository: ShelfRepository,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<ApprovedDukanUiState, ApprovedDukanEffect>(ApprovedDukanUiState()),
    ApprovedDukanInteractionListener {

    init {
        loadShelves()
    }

    override fun onBackButtonClicked() {
        emitEffect(ApprovedDukanEffect.NavigateBack)
    }

    override fun onDismissSnackBar() {
        updateState { copy(showSnackBar = false) }
    }

    override fun onShelfSelected(shelfId: String) {
        val shelf = state.value.availableShelves.find { it.id == shelfId }
        if (shelf != null) {
            val updatedSelectedShelves = state.value.selectedShelves.toMutableSet()
            if (updatedSelectedShelves.contains(shelf)) {
                updatedSelectedShelves.remove(shelf)
            } else {
                updatedSelectedShelves.add(shelf)
            }
            updateState { copy(selectedShelves = updatedSelectedShelves) }
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

    override fun isShelfSelected(): (ShelfUiState) -> Boolean = { shelf ->
        state.value.selectedShelves.contains(shelf)
    }

    override fun onShelfSelected(shelf: ShelfUiState): Boolean {
        val updatedSelectedShelves = state.value.selectedShelves.toMutableSet()
        updatedSelectedShelves.add(shelf)
        updateState { copy(selectedShelves = updatedSelectedShelves) }
        return true
    }

    override fun onShelfDeselected(shelf: ShelfUiState): Boolean {
        val updatedSelectedShelves = state.value.selectedShelves.toMutableSet()
        updatedSelectedShelves.remove(shelf)
        updateState { copy(selectedShelves = updatedSelectedShelves) }
        return true
    }

    override fun onShelfEnabled(shelf: ShelfUiState): Boolean = true

    private fun loadShelves() {
        tryToExecute(
            onStart = { updateState { copy(isLoading = true) } },
            block = { shelfRepository.getMyDukanShelves() },
            onSuccess = { shelves ->
                updateStateWithShelves(
                    shelves,
                    selectFirstShelfByDefault(shelves)
                )
            },
            onError = { handleLoadShelvesError() },
            dispatcher = ioDispatcher
        )
    }

    private fun selectFirstShelfByDefault(shelves: List<net.thechance.mena.dukan.domain.entity.Shelf>): Set<ShelfUiState> {
        return if (shelves.isNotEmpty()) {
            val firstShelf = shelves.first()
            setOf(ShelfUiState(id = firstShelf.id, name = firstShelf.name, imageUrl = ""))
        } else {
            emptySet()
        }
    }

    private fun updateStateWithShelves(
        shelves: List<net.thechance.mena.dukan.domain.entity.Shelf>,
        selectedShelves: Set<ShelfUiState>
    ) {
        val availableShelves = shelves.map { shelf ->
            ShelfUiState(id = shelf.id, name = shelf.name, imageUrl = "")
        }

        updateState {
            copy(
                shelves = shelves,
                availableShelves = availableShelves,
                selectedShelves = selectedShelves,
                productCount = shelves.size,
                isLoading = false
            )
        }
    }

    private fun handleLoadShelvesError() {
        updateState { copy(isLoading = false) }
        showSnackBar()
    }

    private fun showSnackBar() {
        updateState { copy(showSnackBar = true) }
    }
}