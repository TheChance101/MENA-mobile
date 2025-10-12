package net.thechance.mena.dukan.presentation.viewModel.dukans

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.pagination.base.createPagingSource
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class DukansViewModel(
    private val dukanRepository: DukanRepository,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DukansUiState, DukansEffects>(
    initialState = DukansUiState(),
    defaultDispatcher = defaultDispatcher
), DukansInteractionListener {

    override fun onBackClick() {
        emitEffect(DukansEffects.NavigateBack)
    }

    override fun onDukanClick(dukan: DukanUiState) {
        emitEffect(DukansEffects.NavigateToDukanDetails(dukan.id))
    }

    override fun onFavoriteClick(dukan: DukanUiState) {
        tryToExecute(
            block = { toggleFavoriteStatus(dukan) },
            onSuccess = { updateFavoriteState(dukan) }
        )
    }

    private suspend fun toggleFavoriteStatus(dukan: DukanUiState) {
        if (dukan.isFavorite) {
            // TODO remove dukan from favorites
        } else {
            // TODO add dukan to favorites
        }
    }

    private fun updateFavoriteState(dukan: DukanUiState) {
        updateState {
            copy(
                dukans = dukans.copy(
                    items = dukans.items.map { item ->
                        if (item.id == dukan.id) {
                            item.copy(isFavorite = !item.isFavorite)
                        } else {
                            item
                        }
                    }
                )
            )
        }
    }

    fun initialize(categoryId: String): Pager<Int, DukanUiState> {
        val pager = createPager(categoryId)
        loadDukans(pager)
        return pager
    }

    private fun createPager(categoryId: String): Pager<Int, DukanUiState> {
        return createPagingSource(
            mapper = { it.toUiState() }
        ) { pageNumber ->
            dukanRepository.getDukansByCategory(
                categoryId = categoryId,
                page = pageNumber,
                size = 20
            )
        }
    }

    private fun loadDukans(pager: Pager<Int, DukanUiState>) {
        refreshPager(pager)
        collectDukans(pager)
        loadNextPage(pager)
    }

    private fun refreshPager(pager: Pager<Int, DukanUiState>) {
        viewModelScope.launch {
            pager.refresh()
        }
    }

    private fun collectDukans(pager: Pager<Int, DukanUiState>) {
        tryToCollect(
            onStart = ::onLoadingStart,
            block = { pager.flow },
            onCollect = ::onDukansLoaded
        )
    }

    private fun loadNextPage(pager: Pager<Int, DukanUiState>) {
        viewModelScope.launch {
            pager.load()
        }
    }

    private fun onLoadingStart() {
        updateState {
            copy(
                dukansState = DukansState.LOADING,
                dukans = PagingData()
            )
        }
    }

    private fun onDukansLoaded(dukans: PagingData<DukanUiState>) {
        updateState {
            copy(
                dukans = dukans,
                dukansState = getDukansState(dukans)
            )
        }
    }

    private fun getDukansState(dukans: PagingData<DukanUiState>): DukansState {
        return when {
            dukans.isLoading && dukans.items.isEmpty() -> DukansState.LOADING
            dukans.items.isEmpty() -> DukansState.EMPTY
            else -> DukansState.LOADED
        }
    }
}
