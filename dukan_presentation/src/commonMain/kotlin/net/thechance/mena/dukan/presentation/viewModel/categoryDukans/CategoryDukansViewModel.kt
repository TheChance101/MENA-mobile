package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.presentation.util.pagination.PagerOld
import net.thechance.mena.dukan.presentation.util.pagination.PagingDataOld
import net.thechance.mena.dukan.presentation.util.pagination.base.createPagingSource
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukansState

class CategoryDukansViewModel(
    private val dukanDiscoveryRepository: DukanDiscoveryRepository,
    private val savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CategoryDukansUiState, CategoryDukansEffects>(
    initialState = CategoryDukansUiState(),
    defaultDispatcher = defaultDispatcher
), CategoryDukansInteractionListener {

    private var pagerOld: PagerOld<Int, DukanUiState>? = null

    val initializedPagerOld: PagerOld<Int, DukanUiState> by lazy {
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

    private fun toggleFavoriteStatus(dukan: DukanUiState) {
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

    private fun loadDukans(pagerOld: PagerOld<Int, DukanUiState>) {
        refreshPager(pagerOld)
        collectDukans(pagerOld)
        loadNextPage(pagerOld)
    }

    private fun refreshPager(pagerOld: PagerOld<Int, DukanUiState>) {
        viewModelScope.launch {
            pagerOld.refresh()
        }
    }

    private fun collectDukans(pagerOld: PagerOld<Int, DukanUiState>) {
        tryToCollect(
            onStart = ::onLoadingStart,
            block = { pagerOld.flow },
            onCollect = ::onDukansLoaded
        )
    }

    private fun loadNextPage(pagerOld: PagerOld<Int, DukanUiState>) {
        viewModelScope.launch {
            pagerOld.load()
        }
    }

    private fun onLoadingStart() {
        updateState {
            copy(
                dukansState = DukansState.LOADING,
                dukans = PagingDataOld()
            )
        }
    }

    private fun onDukansLoaded(dukans: PagingDataOld<DukanUiState>) {
        updateState {
            copy(
                dukans = dukans,
                dukansState = getDukansState(dukans)
            )
        }
    }

    private fun getDukansState(dukans: PagingDataOld<DukanUiState>): DukansState {
        return when {
            dukans.isLoading && dukans.items.isEmpty() -> DukansState.LOADING
            dukans.items.isEmpty() -> DukansState.EMPTY
            else -> DukansState.LOADED
        }
    }

    private fun initializePager(): PagerOld<Int, DukanUiState> {
        val (categoryId, categoryTitle) = getCategoryArguments()
        updateCategoryState(categoryId, categoryTitle)

        val pager = createDukanPager(categoryId)
        this.pagerOld = pager
        loadDukans(pager)

        return pager
    }

    private fun getCategoryArguments(): Pair<String, String> {
        val categoryId = savedStateHandle.get<String>("categoryId").orEmpty()
        val categoryTitle = savedStateHandle.get<String>("categoryTitle").orEmpty()
        return categoryId to categoryTitle
    }

    private fun updateCategoryState(categoryId: String, categoryTitle: String) {
        updateState {
            copy(
                categoryId = categoryId,
                categoryTitle = categoryTitle
            )
        }
    }

    private fun createDukanPager(categoryId: String): PagerOld<Int, DukanUiState> {
        return createPagingSource(
            mapper = { it.toUiState() }
        ) { pageNumber ->
            dukanDiscoveryRepository.getDukansByCategory(
                categoryId = categoryId,
                page = pageNumber,
                size = 20
            )
        }
    }
}
