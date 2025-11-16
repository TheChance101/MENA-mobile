package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.entity.dukan.Product
import net.thechance.mena.admin_panel.domain.entity.dukan.Shelf
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.Paginator
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class DukanDetailsViewModel(
    @Provided
    private val dukanRepository: DukanRepository,
    @Provided
    private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    BaseViewModel<DukanDetailsScreenState, DukanDetailEffect>(DukanDetailsScreenState()),
    DukanDetailsInteractionListener {

    private lateinit var shelvesPaginator: Paginator<Int, PagedResult<Shelf>>
    private lateinit var productsPaginator: Paginator<Int, List<Product>>

    init {
        getDukanDetails()
        initializeShelvesPaginator()
    }

    override fun onBackButtonClicked() {
        sendEffect(DukanDetailEffect.NavigateBack)
    }

    override fun onChangeDukanStatusButtonClicked() {
        updateState { it.copy(isMapVisible = false) }
        viewModelScope.launch {
            delay(500)
            updateState { it.copy(isDeactivateDukanDialogShown = true) }
        }
    }

    override fun onNextShelvesPageRequested() {
        loadNextShelves()
    }

    override fun onShelfSelected(shelfId: Uuid) {
        if (currentState.selectedShelfId != shelfId.toString()) {
            updateState { it.copy(selectedShelfId = shelfId.toString()) }
            initializeProductsPaginator()
            loadNextProducts()
        }
    }

    override fun onNextProductsPageRequested() {
        loadNextProducts()
    }

    override fun onDeactivateDukanDialogDismissed() {
        updateState {
            it.copy(
                isDeactivateDukanDialogShown = false,
                deactivateReason = "",
                isDeactivateBtnLoading = false,
            )
        }
        viewModelScope.launch {
            delay(500)
            updateState { it.copy(isMapVisible = true) }
        }
    }

    override fun onConfirmDukanDeactivationButtonClicked() {
        //call endpoint
    }

    override fun onDeactivateReasonChanged(reason: String) {
        reason
            .takeIf { it.length < 200 }
            ?.let { reason ->
                updateState { it.copy(deactivateReason = reason) }
            }
    }

    override fun onRetry() {
        updateState { it.copy(errorState = null) }
        getDukanDetails()
        initializeShelvesPaginator()
        loadNextShelves()
    }

    private fun getDukanDetails() {
        tryToExecute(
            callee = { dukanRepository.getDukanDetails() },
            onSuccess = ::onGetDukanDetailsSuccess,
            onError = ::onGetDukanDetailsError,
            onStart = { updateState { it.copy(isDukanDetailsLoading = true) } },
            onFinish = { updateState { it.copy(isDukanDetailsLoading = false) } },
            dispatcher = dispatcher
        )
    }

    private fun onGetDukanDetailsSuccess(dukan: Dukan) {
        updateState { it.copy(dukan = dukan.toUiState()) }
        loadNextShelves()
    }

    private suspend fun onGetDukanDetailsError(error: ErrorState) {
        showSnackBar(
            title = stringProvider.getString(error.getErrorSnackBarTitle()),
            message = stringProvider.getString(error.getErrorSnackBarMsg()),
            isSuccess = false
        )
    }

    private fun initializeShelvesPaginator() {
        shelvesPaginator = Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::onShelvesPaginationLoading,
            onRequest = ::getPagedShelves,
            getNextKey = { currentKey, _ -> currentKey + 1 },
            onError = ::onPaginationError,
            onSuccess = { result, _ -> onGetPagedShelvesSuccess(result) },
            endReached = { _, result -> result.items.isEmpty() || result.items.size < PAGE_SIZE }
        )
    }

    private fun onShelvesPaginationLoading(isLoading: Boolean) {
        updateState { it.copy(isShelvesLoading = isLoading) }
    }

    private suspend fun getPagedShelves(page: Int): PagedResult<Shelf> {
        return dukanRepository.getDukanShelves(
            dukanId = currentState.dukan.id,
            page = page,
            size = PAGE_SIZE
        )
    }

    private fun onGetPagedShelvesSuccess(pagedShelves: PagedResult<Shelf>) {
        if (currentState.selectedShelfId.isEmpty()) {
            pagedShelves.items.firstOrNull()?.let { item ->
                updateState { it.copy(selectedShelfId = item.id.toString()) }
                initializeProductsPaginator()
                loadNextProducts()
            }
        }
        if (currentState.totalShelves.isEmpty()) {
            updateState { it.copy(totalShelves = pagedShelves.totalElements.toString()) }
        }
        updateState {
            it.copy(shelves = it.shelves + pagedShelves.items)
        }
    }

    private fun loadNextShelves() {
        viewModelScope.launch(dispatcher) {
            shelvesPaginator.loadNextItems()
        }
    }

    private fun initializeProductsPaginator() {
        productsPaginator = Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::onProductsPaginationLoading,
            onRequest = ::getPagedProducts,
            getNextKey = { currentKey, _ -> currentKey + 1 },
            onError = ::onPaginationError,
            onSuccess = { result, _ -> onGetPagedProductsSuccess(result) },
            endReached = { _, result -> result.isEmpty() || result.size < PAGE_SIZE }
        )
    }

    private fun onProductsPaginationLoading(isLoading: Boolean) {
        updateState { it.copy(isProductsLoading = isLoading) }
    }

    private suspend fun getPagedProducts(page: Int): List<Product> {
        return dukanRepository.getShelfProducts(
            shelfId = Uuid.parse(currentState.selectedShelfId),
            page = page,
            size = PAGE_SIZE
        ).items
    }

    private fun onGetPagedProductsSuccess(products: List<Product>) {
        updateState {
            it.copy(products = it.products + products)
        }
    }

    private fun loadNextProducts() {
        viewModelScope.launch(dispatcher) {
            productsPaginator.loadNextItems()
        }
    }

    private suspend fun onPaginationError(error: Throwable?) {
        error?.let {
            val errorState = mapError(it)
            updateState { it.copy(errorState = errorState) }
            showSnackBar(
                title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
                message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
                isSuccess = false
            )
        }
    }

    private suspend fun showSnackBar(
        title: String,
        message: String,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    title = title,
                    message = message,
                    isSuccess = isSuccess
                )
            )
        }

        delay(durationMillis)

        hideSnackBar()
    }

    private fun hideSnackBar() {
        updateState { oldState ->
            oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false))
        }
    }

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            else -> ErrorState.UnknownError
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_PAGE = 0
    }
}