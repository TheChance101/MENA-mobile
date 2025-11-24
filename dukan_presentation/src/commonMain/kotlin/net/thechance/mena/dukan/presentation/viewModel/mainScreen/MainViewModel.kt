@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_management_general_error
import mena.dukan_presentation.generated.resources.dukan_managment_loading
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.error_updating_favorites
import mena.dukan_presentation.generated.resources.no_internet_connection
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.model.TopDiscountedDukanPreview
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.toUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi

import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MainViewModel(
    private val dukanManagementRepository: DukanManagementRepository,
    private val dukanDiscoveryRepository: DukanDiscoveryRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainScreenUiState, MainScreenEffect>(
    initialState = MainScreenUiState(),
    defaultDispatcher = dispatcher
), MainInteractionListener {

    private var fetchJob: Job? = null

    private var editorPickState: MutableStateFlow<PagingData<MainScreenUiState.EditorPickDukanUiState>> =
        MutableStateFlow(PagingData.empty())

    init {
        fetchData()
    }

    private fun fetchData() {
        if (fetchJob?.isActive == true) return
        fetchJob = viewModelScope.launch(defaultDispatcher) {
            getDukanState()
            getDukanTopDiscount()
            getCategories()
            loadEditorPicksDukans()
            loadBestNearestDukans()
        }.also { job ->
            job.invokeOnCompletion { fetchJob = null }
        }
    }

    private fun getDukanTopDiscount() {
        tryToExecute(
            block = ::dukanTopDiscountPagingSource,
            onSuccess = ::onGetDukanTopDiscountSuccess,
        )
    }

    private suspend fun dukanTopDiscountPagingSource(): List<TopDiscountedDukanPreview> {
        val page = 0
        val maxSize = 5
        return dukanDiscoveryRepository.getTopDiscountedDukans(
            page = page,
            size = maxSize
        ).items.filter { it.discount > 0 }
    }

    private fun onGetDukanTopDiscountSuccess(dukanTopDiscount: List<TopDiscountedDukanPreview>) {
        updateState { copy(dukanTopDiscount = dukanTopDiscount.map { it.toUiState() }) }
    }

    fun getDukanState() {
        tryToExecute(
            onStart = ::onGetDukanStateStart,
            block = ::getDukanStateBlock,
            onSuccess = ::onGetDukanStateSuccess,
            onError = ::onGetDukanStateError
        )
    }

    private fun onGetDukanStateStart() {
        updateState {
            copy(
                dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Loading),
                isContentLoading = true,
                isConnected = true,
                snackBarState = null
            )
        }
    }

    private suspend fun getDukanStateBlock(): MainScreenUiState.DukanState? {
        return dukanManagementRepository.getMyDukanStatus()?.toUiState()
    }

    private fun onGetDukanStateSuccess(dukanState: MainScreenUiState.DukanState?) {
        updateState { copy(isConnected = true, snackBarState = null) }
        if (dukanState == null) {
            updateState {
                copy(
                    dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.None),

                    )
            }
        } else {
            updateState { copy(dukanState = dukanState) }
        }
    }

    private fun onGetDukanStateError(error: Throwable) {
        updateState { copy(isContentLoading = false) }
        fetchJob?.cancel()
        when (error) {
            is NoSuchItemException -> updateState {
                copy(
                    dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.None)
                )
            }

            is NoInternetException -> {
                updateState {
                    copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Default))
                }
                updateToNoInternetState()
            }

            else -> updateState {
                copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Default))
            }
        }
    }

    private fun getCategories() {
        tryToExecute(
            block = ::getCategoriesBlock,
            onSuccess = ::onGetCategoriesSuccess,
            onError = ::handleGetCategoriesError
        )
    }

    private suspend fun getCategoriesBlock(): List<DukanCategoryUiState> {
        return dukanManagementRepository.getCategories().toUiState()
    }

    private fun onGetCategoriesSuccess(categoryUiState: List<DukanCategoryUiState>) {
        updateState {
            copy(
                categories = categoryUiState,
                snackBarState = null,
                isContentLoading = false
            )
        }
    }

    private fun handleGetCategoriesError(error: Throwable) {
        updateState { copy(isContentLoading = false) }
        fetchJob?.cancel()
        when (error) {
            is NoInternetException -> {
                updateToNoInternetState()
            }

            else -> {
                showSnackBar(
                    message = Res.string.error_general,
                    type = SnackBarType.ERROR,
                )
            }
        }
    }

    fun loadEditorPicksDukans() {
        tryToCollect(
            block = ::createLoadEditorPagingSource,
            onCollect = ::onLoadedEditorPicksDukan,
            onError = ::handleGetEditorPicksDukanError
        )
    }

    private fun createLoadEditorPagingSource(): Flow<PagingData<MainScreenUiState.EditorPickDukanUiState>> {
        return createPagingSourceFlow(mapper = { it.toEditorPickUiState() }) { currentPage, pageSize ->
            dukanDiscoveryRepository.getEditorPicksDukans(
                page = currentPage,
                size = pageSize
            ).items
        }
    }

    private fun onLoadedEditorPicksDukan(dukans: PagingData<MainScreenUiState.EditorPickDukanUiState>) {
        editorPickState.value = dukans
        updateState {
            copy(
                editorPickDukans = editorPickState,
                snackBarState = null,
            )
        }
    }

    private fun handleGetEditorPicksDukanError(error: Throwable) {
        updateState { copy(isContentLoading = false) }
        fetchJob?.cancel()
        when (error) {
            is NoInternetException -> {
                updateToNoInternetState()
            }

            else -> {
                showSnackBar(
                    message = Res.string.error_general,
                    type = SnackBarType.ERROR,
                )
            }
        }
    }

    private fun loadBestNearestDukans() {
        tryToCollect(
            block = ::createLoadBestDukanPagingSource,
            onCollect = ::onLoadedBestNearestDukans,
            onError = ::handleGetBestNearestDukansError
        )
    }

    private fun createLoadBestDukanPagingSource(): Flow<PagingData<MainScreenUiState.BestNearestDukanUiState>> {
        return createPagingSourceFlow(mapper = { it.toBestNearestUiState() }) { currentPage, pageSize ->
            dukanDiscoveryRepository.getBestAroundDukans(
                page = currentPage,
                size = pageSize
            ).items
        }
    }

    private fun onLoadedBestNearestDukans(dukans: PagingData<MainScreenUiState.BestNearestDukanUiState>) {
        updateState {
            copy(
                bestNearestDukans = flowOf(dukans),
                snackBarState = null,
                isContentLoading = false
            )
        }
    }

    private fun handleGetBestNearestDukansError(error: Throwable) {
        updateState { copy(isContentLoading = false) }
        fetchJob?.cancel()
        when (error) {
            is NoInternetException -> {
                updateToNoInternetState()
            }

            else -> {
                showSnackBar(
                    message = Res.string.error_general,
                    type = SnackBarType.ERROR,
                )
            }
        }
    }

    private fun updateToNoInternetState() {
        showSnackBar(message = Res.string.no_internet_connection, type = SnackBarType.ERROR)
        updateState { copy(isConnected = false) }
    }

    override fun onDukanButtonClicked() {
        when (state.value.dukanState.status) {
            DukanStatusUi.None -> emitEffect(MainScreenEffect.NavigateToAddDukanScreen)
            DukanStatusUi.Pending -> emitEffect(MainScreenEffect.NavigateToPendingDukanScreen)
            DukanStatusUi.Approved -> emitEffect(MainScreenEffect.NavigateToManageDukanScreen)
            DukanStatusUi.Default -> showSnackBar(
                message = Res.string.dukan_management_general_error,
                type = SnackBarType.ERROR
            )

            DukanStatusUi.Loading -> showSnackBar(
                message = Res.string.dukan_managment_loading,
                type = SnackBarType.ERROR
            )
        }
    }

    override fun onViewMoreClicked() {
        emitEffect(MainScreenEffect.NavigateToDukansCategoriesScreen)
    }

    override fun onRetryClicked() {
        fetchData()
    }

    override fun onSnackBarDismissed() {
        updateState {
            copy(snackBarState = null)
        }
    }

    override fun onSelectedCategoryClicked(categoryId: String, categoryName: String) {
        emitEffect(MainScreenEffect.NavigateToDukansScreenByCategory(categoryId, categoryName))
    }

    override fun onNearestDukanClicked(dukanId: String) {
        emitEffect(MainScreenEffect.NavigateToSelectedDukan(dukanId))
    }

    override fun onEditorPickDukanClicked(dukanId: String) {
        emitEffect(MainScreenEffect.NavigateToSelectedDukan(dukanId))
    }


    override fun onFavoriteDukanClicked(dukanId: String) {
        setFavoriteState(dukanId)
        tryToExecute(
            block = { dukanManagementRepository.updateFavoriteDukanStatus(dukanId) },
            onError = ::onErrorUpdateDukanFavoriteStatus
        )
    }

    override fun onShopNowClicked(dukanId: Uuid) {
        emitEffect(MainScreenEffect.NavigateToSelectedDukan(dukanId.toString()))
    }

    fun setFavoriteState(
        dukanId: String,
    ) {
        val currentData = editorPickState.value
        val updatedData = currentData.map { dukan ->
            if (dukan.id == dukanId) dukan.copy(isFavorite = !dukan.isFavorite) else dukan
        }
        editorPickState.value = updatedData
        updateState { copy(editorPickDukans = editorPickState) }
    }

    private fun onErrorUpdateDukanFavoriteStatus(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.error_updating_favorites
        }
        showSnackBar(message = messageRes,type = SnackBarType.ERROR)
    }
    override fun onSearchButtonClicked() {
        emitEffect(MainScreenEffect.NavigateToSearchScreen)
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                )
            )
        }
    }
}