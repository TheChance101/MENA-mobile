package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.pagination.base.createPagingSource
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.DukanCategoryUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.toUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi

class MainViewModel(
    private val dukanRepository: DukanRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainScreenUiState, MainEffect>(
    initialState = MainScreenUiState(),
    defaultDispatcher = dispatcher
), MainInteractionListener {
    lateinit var bestNearestDukanPager: Pager<Int, MainScreenUiState.BestNearestDukanUiState>
    lateinit var editorPickDukanPager: Pager<Int, MainScreenUiState.EditorPickDukanUiState>

    init {
        initPagers()
        getDukanState()
        getCategories()
        getEditorPicksDukans()
        getBestNearestDukans()
    }

    private fun getEditorPicksDukans() {
        tryToCollect(
            onStart = ::onEditorPickDukanLoading,
            block = { editorPickDukanPager.flow },
            onCollect = ::onLoadedEditorPicksDukan
        )
        viewModelScope.launch(defaultDispatcher) {
            editorPickDukanPager.load()
        }
    }


    private fun onEditorPickDukanLoading() {
        updateState {
            copy(
                editorPickDukanState = MainScreenUiState.EditorPickDukanStatus.LOADING,
                editorPickDukans = PagingData()
            )
        }
    }

    private fun onLoadedEditorPicksDukan(dukans: PagingData<MainScreenUiState.EditorPickDukanUiState>) {
        val loadedBestNearestDukans = when {
            dukans.isLoading && dukans.items.isEmpty() -> MainScreenUiState.EditorPickDukanStatus.LOADING
            else -> MainScreenUiState.EditorPickDukanStatus.LOADED
        }
        updateState {
            copy(
                editorPickDukans = dukans,
                editorPickDukanState = loadedBestNearestDukans
            )
        }
    }

    private fun getBestNearestDukans() {
        tryToCollect(
            onStart = ::onBestNearestDukanLoading,
            block = { bestNearestDukanPager.flow },
            onCollect = ::onLoadedBestNearestDukans
        )
        viewModelScope.launch(defaultDispatcher) {
            bestNearestDukanPager.load()
        }
    }

    private fun onBestNearestDukanLoading() {
        updateState {
            copy(
                bestNearestDukans = PagingData(),
                bestNearestDukanState = MainScreenUiState.BestNearestDukanStatus.LOADING
            )
        }
    }

    private fun onLoadedBestNearestDukans(dukans: PagingData<MainScreenUiState.BestNearestDukanUiState>) {
        val loadedBestNearestDukans = when {
            dukans.isLoading && dukans.items.isEmpty() -> MainScreenUiState.BestNearestDukanStatus.LOADING
            else -> MainScreenUiState.BestNearestDukanStatus.LOADED
        }
        updateState {
            copy(
                bestNearestDukans = dukans,
                bestNearestDukanState = loadedBestNearestDukans
            )
        }
    }

    private fun getCategories() {
        tryToExecute(
            onStart = {
                setLoadingState()
            },
            block = ::getCategoriesBlock,
            onSuccess = ::onGetCategoriesSuccess,
            onError = ::onGetCategoriesError
        )
    }

    private fun onGetCategoriesError(error: Throwable) {
        updateState {
            copy(
                errorMessage = error.message
            )
        }
    }

    private suspend fun getCategoriesBlock(): List<DukanCategoryUiState> {
        return dukanRepository.getCategories().toUiState()
    }

    private fun onGetCategoriesSuccess(categoryUiState: List<DukanCategoryUiState>) {
        updateState {
            copy(
                categories = categoryUiState
            )
        }
    }

    private fun getDukanState() {
        tryToExecute(
            onStart = ::setLoadingState,
            block = ::getDukanStateBlock,
            onSuccess = ::onGetDukanStateSuccess,
            onError = ::onGetDukanStateError
        )
    }

    private fun setLoadingState() {
        updateState { copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Loading)) }
    }

    private suspend fun getDukanStateBlock(): MainScreenUiState.DukanState? {
        return dukanRepository.getMyDukanStatus()?.toUiState()
    }

    private fun onGetDukanStateSuccess(dukanState: MainScreenUiState.DukanState?) {
        if (dukanState == null) {
            updateState { copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.None)) }
        } else {
            updateState { copy(dukanState = dukanState) }
        }
    }

    private fun onGetDukanStateError(error: Throwable) {
        when (error) {
            is NoSuchItemException -> updateState {
                copy(
                    errorMessage = error.message,
                    dukanState = MainScreenUiState.DukanState(
                        status = DukanStatusUi.None
                    )
                )
            }
        }
    }

    override fun onDukanButtonClicked() {
        when (state.value.dukanState.status) {
            DukanStatusUi.None -> emitEffect(MainEffect.NavigateToAddDukanScreen)
            DukanStatusUi.Pending -> emitEffect(MainEffect.NavigateToPendingDukanScreen)
            DukanStatusUi.Approved -> emitEffect(MainEffect.NavigateToManageDukanScreen)
            DukanStatusUi.Loading -> {}
        }
    }

    override fun onViewMoreButtonClick() {
        emitEffect(MainEffect.NavigateCategoryToScreen)
    }

    override fun onCategorySelectedClick(categoryId: String, categoryName: String) {
        emitEffect(MainEffect.NavigateToDukansScreenByCategory(categoryId, categoryName))
    }

    override fun onNearestDukanClick(dukanId: String) {
        emitEffect(MainEffect.NavigateSelectedDukan(dukanId))
    }

    override fun onEditorPickDukanClick(dukanId: String) {
        emitEffect(MainEffect.NavigateSelectedDukan(dukanId))
    }

    fun initPagers() {
        bestNearestDukanPager = createPagingSource(
            mapper = { it.toBestNearestUiState() }
        ) { currentPage ->
            dukanRepository.getBestAroundDukans(
                page = currentPage,
                size = 20
            )
        }

        editorPickDukanPager = createPagingSource(
            mapper = { it.toEditorPickUiState() }
        ) { currentPage ->
            dukanRepository.getEditorPicksDukans(
                page = currentPage,
                size = 20
            )
        }
    }

}