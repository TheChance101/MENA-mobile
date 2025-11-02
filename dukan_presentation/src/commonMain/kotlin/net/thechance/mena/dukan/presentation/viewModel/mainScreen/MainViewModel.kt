package net.thechance.mena.dukan.presentation.viewModel.mainScreen

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_management_general_error
import mena.dukan_presentation.generated.resources.dukan_managment_loading
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.no_internet_connection
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.repository.DukanDiscoveryRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.toUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState.DukanStatusUi

import org.jetbrains.compose.resources.StringResource

class MainViewModel(
    private val dukanManagementRepository: DukanManagementRepository,
    private val dukanDiscoveryRepository: DukanDiscoveryRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<MainScreenUiState, MainScreenEffect>(
    initialState = MainScreenUiState(),
    defaultDispatcher = dispatcher
), MainInteractionListener {

    init {
        fetchData()
    }

    private fun fetchData() {
        getDukanState()
        getCategories()
        loadBestNearestDukans()
        loadEditorPicksDukans()
    }

    private fun getDukanState() {
        tryToExecute(
            onStart = ::onGetDukanStateStart,
            block = ::getDukanStateBlock,
            onSuccess = ::onGetDukanStateSuccess,
            onError = ::onGetDukanStateError
        )
    }

    private fun onGetDukanStateStart() {
        updateState { copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Loading)) }
    }

    private suspend fun getDukanStateBlock(): MainScreenUiState.DukanState? {
        return dukanManagementRepository.getMyDukanStatus()?.toUiState()
    }

    private fun onGetDukanStateSuccess(dukanState: MainScreenUiState.DukanState?) {
        if (dukanState == null) {
            updateState {
                copy(
                    dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.None),
                    isConnected = true,
                    snackBarState = null
                )
            }
        } else {
            updateState { copy(dukanState = dukanState, isConnected = true) }
        }
    }

    private fun onGetDukanStateError(error: Throwable) {
        when (error) {
            is NoSuchItemException -> updateState {
                copy(
                    dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.None)
                )
            }

            is NoInternetException -> {
                updateState {
                    copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Default))
                    updateToNoInternetState()
                }

            }

            else -> updateState {
                copy(dukanState = MainScreenUiState.DukanState(status = DukanStatusUi.Default))
            }
        }
    }

    private fun getCategories() {
        tryToExecute(
            onStart = ::onGetCategoriesStart,
            block = ::getCategoriesBlock,
            onSuccess = ::onGetCategoriesSuccess,
            onError = ::handleGetCategoriesError
        )
    }

    private fun onGetCategoriesStart(){
        updateState { copy(isCategoriesLoading = true) }
    }

    private suspend fun getCategoriesBlock(): List<DukanCategoryUiState> {
        return dukanManagementRepository.getCategories().toUiState()
    }

    private fun onGetCategoriesSuccess(categoryUiState: List<DukanCategoryUiState>) {
        updateState {
            copy(
                categories = categoryUiState,
                snackBarState = null,
                isCategoriesLoading = false
            )
        }
    }

    private fun handleGetCategoriesError(error: Throwable) {
        when (error) {
            is NoInternetException -> updateState {
                updateToNoInternetState()
            }

            else -> {
                showSnackBar(
                    message = Res.string.error_general,
                    type = SnackBarType.ERROR,
                )
                updateState {
                    copy(
                        isCategoriesLoading = false,
                    )
                }
            }
        }
    }

    private fun loadBestNearestDukans() {
        tryToCollect(
            onStart = ::onGetBestNearestDukanStart,
            block = ::createLoadBestDukanPagingSource,
            onCollect = ::onLoadedBestNearestDukans,
            onError = ::handleGetBestNearestDukansError
        )
    }

    private fun onGetBestNearestDukanStart(){
        updateState { copy(isBestNearestDukanLoading = true) }
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
                isBestNearestDukanLoading = false
            )
        }
    }

    private fun handleGetBestNearestDukansError(error: Throwable) {
        when (error) {
            is NoInternetException -> updateState {
                updateToNoInternetState()
            }

            else -> {
                showSnackBar(
                    message = Res.string.error_general,
                    type = SnackBarType.ERROR,
                )
                updateState {
                    copy(
                        isBestNearestDukanLoading = false,
                    )
                }
            }
        }
    }

    private fun loadEditorPicksDukans() {
        tryToCollect(
            onStart = ::onGetEditorPicksDukanStart,
            block = ::createLoadEditorPagingSource,
            onCollect = ::onLoadedEditorPicksDukan,
            onError = ::handleGetEditorPicksDukanError
        )
    }

    private fun onGetEditorPicksDukanStart(){
        updateState { copy(isEditorPickDukanLoading = true) }
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
        updateState {
            copy(
                editorPickDukans = flowOf(dukans),
                snackBarState = null,
                isEditorPickDukanLoading = false
            )
        }
    }

    private fun handleGetEditorPicksDukanError(error: Throwable) {
        when (error) {
            is NoInternetException -> updateState {
                updateToNoInternetState()
            }

            else -> {
                showSnackBar(
                    message = Res.string.error_general,
                    type = SnackBarType.ERROR,
                )
                updateState {
                    copy(
                        isEditorPickDukanLoading = false,
                    )
                }
            }
        }
    }

    private fun MainScreenUiState.updateToNoInternetState(): MainScreenUiState = copy(
        isConnected = false,
        snackBarState = SnackBarUiState(
            message = Res.string.no_internet_connection,
            snackBarType = SnackBarType.ERROR
        )
    )

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
        emitEffect(MainScreenEffect.NavigateCategoryToScreen)
    }

    override fun onRetryClicked() {
        fetchData()
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(snackBarState = null)
        }
    }

    override fun onCategorySelectedClicked(categoryId: String, categoryName: String) {
        emitEffect(MainScreenEffect.NavigateToDukansScreenByCategory(categoryId, categoryName))
    }

    override fun onNearestDukanClicked(dukanId: String) {
        emitEffect(MainScreenEffect.NavigateSelectedDukan(dukanId))
    }

    override fun onEditorPickDukanClicked(dukanId: String) {
        emitEffect(MainScreenEffect.NavigateSelectedDukan(dukanId))
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