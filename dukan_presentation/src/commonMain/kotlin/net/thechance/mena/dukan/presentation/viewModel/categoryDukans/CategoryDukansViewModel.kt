package net.thechance.mena.dukan.presentation.viewModel.categoryDukans

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState

class CategoryDukansViewModel(
    private val dukanDiscoveryRepository: DukanDiscoveryRepository,
    private val dukanManagementRepository: DukanManagementRepository,
    private val savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CategoryDukansUiState, CategoryDukansEffects>(
    initialState = CategoryDukansUiState(),
    defaultDispatcher = defaultDispatcher
), CategoryDukansInteractionListener {

    private val dukansState: MutableStateFlow<PagingData<DukanUiState>> =
        MutableStateFlow(PagingData.empty())

    init {
        loadCategory()
    }

    override fun onBackClicked() {
        emitEffect(CategoryDukansEffects.NavigateBack)
    }

    override fun onDukanClicked(dukan: DukanUiState) {
        emitEffect(CategoryDukansEffects.NavigateToDukanDetails(dukan.id))
    }

    override fun onFavoriteDukanClicked(dukanId: String) {
        updateFavoriteDukanPagingData(dukanId = dukanId )
        tryToExecute(
            block = { dukanManagementRepository.updateFavoriteDukanStatus(dukanId) },
        )
    }

    private fun updateFavoriteDukanPagingData(
        dukanId: String,
    ) {
        val currentData = dukansState.value
        val updatedData = currentData.map { dukan ->
            if (dukan.id == dukanId) dukan.copy(isFavorite = !dukan.isFavorite) else dukan
        }
        dukansState.value = updatedData
        updateState { copy(dukans = dukansState) }
    }

    override fun onRetryClicked() {
        loadCategory()
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
        dukansState.value = dukans
        updateState {
            copy(
                dukans = dukansState
            )
        }
    }

    fun loadCategory() {
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
