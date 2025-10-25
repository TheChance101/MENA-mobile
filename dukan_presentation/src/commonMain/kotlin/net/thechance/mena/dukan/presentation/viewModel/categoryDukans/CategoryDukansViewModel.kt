package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState

class CategoryDukansViewModel(
    private val dukanDiscoveryRepository: DukanDiscoveryRepository,
    private val savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CategoryDukansUiState, CategoryDukansEffects>(
    initialState = CategoryDukansUiState(),
    defaultDispatcher = defaultDispatcher
), CategoryDukansInteractionListener {

    init {
        loadCategory()
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
        // ToDo update favorite state in ui
    }

    private fun collectDukans(categoryId: String) {
        tryToCollect(
            block = {
                createPagingSourceFlow(
                    mapper = { it.toUiState() }
                ) { pageNumber, pageSize ->
                    dukanDiscoveryRepository.getDukansByCategory(
                        categoryId = categoryId,
                        page = pageNumber,
                        size = 20
                    ).items
                }
            },
            onCollect = ::onDukansLoaded

        )
    }

    private fun onDukansLoaded(dukans: PagingData<DukanUiState>) {
        updateState {
            copy(
                dukans = flowOf(dukans),
            )
        }
    }

    private fun loadCategory() {
        val (categoryId, categoryTitle) = getCategoryArguments()
        updateCategoryState(categoryId, categoryTitle)
        collectDukans(categoryId)
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
}
