package net.thechance.mena.admin_panel.presentation.screen.dukan_requests

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.model.DukanQueryParams
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_approved_successfully
import net.thechance.mena.admin_panel.resources.dukan_rejected_successfully
import net.thechance.mena.admin_panel.resources.status_updated_title
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class DukanRequestsViewModel(
    @Provided private val dukanRepository: DukanRepository,
    @Provided private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DukanRequestsScreenState, Unit>(
    DukanRequestsScreenState()
), DukanRequestsInteractionListener {

    init {
        getRequestedDukans()
    }

    override fun onSortClicked(type: DukanRequestsScreenState.SortType) {
        val newDirection = if (currentState.sort.type == type) {
            currentState.sort.direction.toggle()
        } else {
            DukanRequestsScreenState.SortDirection.ASC
        }
        updateState {
            it.copy(
                sort = DukanRequestsScreenState.SortState(
                    type = type,
                    direction = newDirection
                ),
                pageInfo = it.pageInfo.copy(page = 0)
            )
        }
        getRequestedDukans()
    }

    override fun onViewDetailsClicked(selectedDukan: DukanRequestsScreenState.DukanItem) {
        updateState { it.copy(isDukanDetailsShown = true, selectedDukan = selectedDukan) }
    }

    override fun onRetryClicked() {
        getRequestedDukans()
    }

    override fun onPageChanged(page: Int) {
        updateState { it.copy(pageInfo = it.pageInfo.copy(page = page)) }
        getRequestedDukans()
    }

    override fun onApproveDukanClicked() {
        val selectedDukanId = currentState.selectedDukan?.id ?: return
        tryToExecute(
            callee = {
                dukanRepository.updateDukanStatus(
                    dukanId = selectedDukanId,
                    status = Dukan.Status.APPROVED,
                    message = currentState.rejectReason
                )
            },
            onSuccess = { onDukanApprovedSuccess() },
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    override fun onRejectDukanClicked() {
        onDukanDetailsDismissed()
        viewModelScope.launch {
            delay(100)
            updateState { it.copy(isRejectDialogShown = true) }
        }
    }

    override fun onRejectDukanDialogDismissed() {
        updateState {
            it.copy(
                isRejectDialogShown = false
            )
        }
    }

    override fun onRejectDukanConfirmed() {
        val selectedDukanId = currentState.selectedDukan?.id ?: return
        tryToExecute(
            callee = {
                dukanRepository.updateDukanStatus(
                    dukanId = selectedDukanId,
                    status = Dukan.Status.REJECTED,
                    message = currentState.rejectReason
                )
            },
            onStart = { updateState { it.copy(isRejectButtonLoading = true) } },
            onFinish = { updateState { it.copy(isRejectButtonLoading = false) } },
            onSuccess = { onSuccessDukanRejected() },
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    override fun onRejectionMessageChanged(reason: String) {
        reason.takeIf { it.length < 200 }?.let { reason ->
            updateState { it.copy(rejectReason = reason) }
        }
    }

    override fun onDukanDetailsDismissed() {
        updateState { it.copy(isDukanDetailsShown = false) }
    }

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            else -> ErrorState.UnknownError
        }
    }

    private fun onDukanApprovedSuccess() {
        onDukanDetailsDismissed()
        refetchCurrentPage()
        viewModelScope.launch {
            showSnackBar(
                title = stringProvider.getString(Res.string.status_updated_title),
                message = stringProvider.getString(Res.string.dukan_approved_successfully),
                isSuccess = true
            )
        }
    }

    private fun onSuccessDukanRejected() {
        onRejectDukanDialogDismissed()
        refetchCurrentPage()
        viewModelScope.launch {
            showSnackBar(
                title = stringProvider.getString(Res.string.status_updated_title),
                message = stringProvider.getString(Res.string.dukan_rejected_successfully),
                isSuccess = true
            )
        }
    }

    private fun refetchCurrentPage() {
        val queryParams = getDukanQueryParams()
        tryToExecute(
            callee = { dukanRepository.getDukans(queryParams) },
            onSuccess = ::onGetRequestedDukansSuccess,
            onError = ::onError,
            onFinish = ::onGetRequestedDukansFinish,
            dispatcher = dispatcher
        )
    }

    private suspend fun onError(errorState: ErrorState) {
        updateState { it.copy(errorState = errorState) }
        showSnackBar(
            title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
            message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
            isSuccess = false
        )
    }

    private suspend fun showSnackBar(
        title: String,
        message: String,
        isSuccess: Boolean,
        durationMillis: Long = DURATION_MILLIS
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

    private fun getRequestedDukans() {
        val queryParams = getDukanQueryParams()
        tryToExecute(
            callee = { dukanRepository.getDukans(queryParams) },
            onSuccess = ::onGetRequestedDukansSuccess,
            onError = ::onError,
            onStart = ::onGetRequestedDukansStart,
            onFinish = ::onGetRequestedDukansFinish,
            dispatcher = dispatcher
        )
    }

    private fun onGetRequestedDukansStart() {
        updateState { it.copy(isLoading = true) }
    }

    private fun onGetRequestedDukansFinish() {
        updateState { it.copy(isLoading = false, isInitialLoading = false) }
    }


    private fun onGetRequestedDukansSuccess(result: PagedResult<Dukan>) {
        updateState {
            it.copy(
                dukans = result.items.mapIndexed { index, dukan ->
                    dukan.toUIState(
                        currentPage = result.currentPage,
                        indexInList = index
                    )
                },
                totalDukanRequests = result.totalElements,
                pageInfo = DukanRequestsScreenState.DukanPageInfo(
                    page = result.currentPage,
                    totalPages = result.totalPages
                ),
                errorState = null
            )
        }
    }

    private fun getDukanQueryParams(): DukanQueryParams {
        return DukanQueryParams(
            sortType = currentState.sort.type.toEntity(),
            sortDirection = currentState.sort.direction.toEntity(),
            status = Dukan.Status.PENDING,
            page = currentState.pageInfo.page,
            size = PAGE_SIZE,
            searchInput = null,
            activationStatus = null
        )
    }

    private companion object {
        const val PAGE_SIZE = 8
        const val DURATION_MILLIS = 3000L
    }
}