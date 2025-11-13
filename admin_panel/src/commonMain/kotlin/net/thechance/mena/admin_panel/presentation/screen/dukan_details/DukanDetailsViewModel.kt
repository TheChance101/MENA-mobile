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

    private lateinit var shelvesPaginator: Paginator<Int, List<Shelf>>
    private lateinit var productsPaginator: Paginator<Int, List<Product>>

    init {
        getDukanDetails()
        initializeShelvesPaginator()
        loadNextShelves()
    }

    override fun onBackBtnClicked() {
        sendEffect(DukanDetailEffect.NavigateBack)
    }

    override fun onChangeDukanStatusBtnClicked() {
        updateState { it.copy(isDeactivateDukanDialogShown = true) }
    }

    override fun onNextShelvesPageRequested() {
        loadNextShelves()
    }

    override fun onShelfSelected(shelfId: String) {
        if (currentState.selectedShelfId != shelfId) {
            updateState { it.copy(selectedShelfId = shelfId) }
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
    }

    override fun onConfirmDukanDeactivationBtnClicked() {
        //call endpoint
    }

    override fun onDeactivateReasonChanged(reason: String) {
        reason
            .takeIf { it.length < 200 }
            ?.let { reason ->
                updateState { it.copy(deactivateReason = reason) }
            }
    }

    private fun getDukanDetails() {
        tryToExecute(
            callee = { dukanRepository.getDukanDetails(Uuid.parse("3e2ac1b3-e322-465a-b454-1af7625ffae9")) },
            onSuccess = ::onGetDukanDetailsSuccess,
            onError = ::onGetDukanDetailsError,
            dispatcher = dispatcher
        )
    }

    private fun onGetDukanDetailsSuccess(dukan: Dukan) {
        updateState { it.copy(dukan = dukan.toUi()) }
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
            onLoadUpdated = {},
            onRequest = ::getPagedShelves,
            getNextKey = { currentKey, _ -> currentKey + 1 },
            onError = {},
            onSuccess = { result, _ -> onGetPagedShelvesSuccess(result) },
            endReached = { _, result -> result.isEmpty() || result.size < PAGE_SIZE }
        )
    }

    private suspend fun getPagedShelves(page: Int): List<Shelf> {
        return dukanRepository.getDukanShelves(
            dukanId = Uuid.parse("3e2ac1b3-e322-465a-b454-1af7625ffae9"),
            page = page,
            size = PAGE_SIZE
        ).items
    }

    private fun onGetPagedShelvesSuccess(shelves: List<Shelf>) {
        updateState {
            it.copy(shelves = it.shelves + shelves)
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
            onLoadUpdated = {},
            onRequest = ::getPagedProducts,
            getNextKey = { currentKey, _ -> currentKey + 1 },
            onError = {},
            onSuccess = { result, _ -> onGetPagedProductsSuccess(result) },
            endReached = { _, result -> result.isEmpty() || result.size < PAGE_SIZE }
        )
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