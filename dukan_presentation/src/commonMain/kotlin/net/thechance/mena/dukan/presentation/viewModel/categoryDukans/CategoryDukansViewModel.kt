package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.lifecycle.SavedStateHandle
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
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukansState
class CategoryDukansViewModel(
    private val dukanRepository: DukanRepository,
    private val savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CategoryDukansUiState, CategoryDukansEffects>(
    initialState = CategoryDukansUiState(),
    defaultDispatcher = defaultDispatcher
), CategoryDukansInteractionListener {

    private var pager: Pager<Int, DukanUiState>? = null

    val initializedPager: Pager<Int, DukanUiState> by lazy {
        initializePager()
    }

    override fun onBackClick() {
        emitEffect(CategoryDukansEffects.NavigateBack)
    }

    override fun onDukanClick(dukan: DukanUiState) {
        emitEffect(CategoryDukansEffects.NavigateToDukanDetails(dukan.id))
    }

    override fun onFavoriteClick(dukan: DukanUiState) {
        tryToExecute(
            block = { toggleFavoriteStatus(dukan) },
            onSuccess = { updateFavoriteState(dukan) }
        )
    }

    private fun initializePager(): Pager<Int, DukanUiState> {
        val categoryId = savedStateHandle.get<String>("categoryId") ?: ""
        val categoryTitle = savedStateHandle.get<String>("categoryTitle") ?: ""

        updateState {
            copy(
                categoryId = categoryId,
                categoryTitle = categoryTitle
            )
        }

        val pager = createPagingSource(
            mapper = { it.toUiState() }
        ) { pageNumber ->
            dukanRepository.getDukansByCategory(
                categoryId = categoryId,
                page = pageNumber,
                size = 20
            )
        }

        this.pager = pager
        loadDukans(pager)
        return pager
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
                    items = dukans.items.toggleFavoriteBest(dukan.id)
                )
            )
        }
    }

    private fun List<DukanUiState>.toggleFavoriteBest(dukanId: String): List<DukanUiState> {
        return map { item ->
            if (item.id == dukanId) {
                item.copy(isFavorite = !item.isFavorite)
            } else {
                item
            }
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

    private fun getDukansState(dukans: PagingData<DukanUiState>): CategoryDukansUiState.DukansState {
        return when {
            dukans.isLoading && dukans.items.isEmpty() -> DukansState.LOADING
            dukans.items.isEmpty() -> DukansState.EMPTY
            else -> DukansState.LOADED
        }
    }
}
