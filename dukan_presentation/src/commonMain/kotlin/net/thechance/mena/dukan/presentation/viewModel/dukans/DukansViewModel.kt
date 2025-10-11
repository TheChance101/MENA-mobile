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

    fun initialize(categoryId: String): Pager<Int, DukanUiState> {
        val pager = createPagingSource(
            mapper = { it.toUiState() },
        ) { pageNumber ->
            dukanRepository.getDukansByCategory(
                categoryId = categoryId,
                page = pageNumber,
                size = 20
            )
        }
        loadDukans(pager)
        return pager
    }

    private fun loadDukans(pager: Pager<Int, DukanUiState>) {
        viewModelScope.launch {
            pager.refresh()
        }
        loadDukansFromRepository(pager)
        viewModelScope.launch {
            pager.refresh()
        }
    }

    private fun loadDukansFromRepository(pager: Pager<Int, DukanUiState>) {
        tryToCollect(
            onStart = {
                updateState {
                    copy(
                        dukansState = DukansState.LOADING,
                        dukans = PagingData()
                    )
                }
            },
            block = { pager.flow },
            onCollect = ::onDukansLoaded,
        )
        viewModelScope.launch {
            pager.load()
        }
    }

    private fun onDukansLoaded(dukans: PagingData<DukanUiState>) {
        val newState = when {
            dukans.isLoading && dukans.items.isEmpty() -> DukansState.LOADING
            dukans.items.isEmpty() -> DukansState.EMPTY
            else -> DukansState.LOADED
        }
        updateState {
            copy(
                dukans = dukans,
                dukansState = newState
            )
        }
    }

}
