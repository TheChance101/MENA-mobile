package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import kotlinx.coroutines.CoroutineDispatcher
import net.thechance.mena.dukan.domain.entity.Shelf
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
            onSuccess = { shelves -> handleShelvesLoaded(shelves) },
            onError = {
                updateState {
                    copy(
                        isLoading = false,
                        showSnackBar = true
                    )
                }
            },
            dispatcher = ioDispatcher
        )
    }

    private fun handleShelvesLoaded(shelves: List<Shelf>) {
        updateState {
            copy(
                shelves = shelves,
                availableShelves = shelves.map { ShelfUiState(id = it.id, name = it.name) },
                selectedShelves = selectFirstShelfByDefault(shelves),
                productCount = shelves.size,
                isLoading = false
            )
        }
    }

    private fun selectFirstShelfByDefault(shelves: List<net.thechance.mena.dukan.domain.entity.Shelf>): Set<ShelfUiState> {
        return if (shelves.isNotEmpty()) {
            val firstShelf = shelves.first()
            setOf(ShelfUiState(id = firstShelf.id, name = firstShelf.name))
        } else {
            emptySet()
        }
    }
}