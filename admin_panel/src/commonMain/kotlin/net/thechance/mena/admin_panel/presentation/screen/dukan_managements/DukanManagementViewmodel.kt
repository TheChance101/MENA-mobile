package net.thechance.mena.admin_panel.presentation.screen.dukan_managements

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.model.DukanQueryParams
import net.thechance.mena.admin_panel.domain.model.DukansSortType
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.model.SortDirection
import net.thechance.mena.admin_panel.domain.repository.dukan.DukanRepository
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class DukanManagementViewmodel(
    @Provided
    private val dukanRepository: DukanRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DukanManagementScreenState, DukanManagementEffect>(
    DukanManagementScreenState()
), DukanManagementInteractionListener {
    private var dukans: List<Dukan> = emptyList()
    private var searchJob: Job? = null

    init {
        getDukans()
    }

    override fun onSearchQueryChange(query: String) {
        if (query == currentState.query) return

        updateState { it.copy(query = query) }
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            updateState { it.copy(pageInfo = it.pageInfo.copy(page = 0)) }
            getDukans()
        }
    }

    override fun onClearQueryClicked() {
        updateState {
            it.copy(query = "")
        }
        getDukans()
    }

    override fun onRetryClicked() {
        getDukans()
    }

    override fun onPageChanged(page: Int) {
        updateState { it.copy(pageInfo = it.pageInfo.copy(page = page)) }
        getDukans()
    }

    override fun onViewDetailsClicked(dukanId: Uuid) {
        val dukan = dukans.find { it.id == dukanId } ?: return
        dukanRepository.storeDukanDetails(dukan)
        sendEffect(DukanManagementEffect.NavigateToDukanDetails(dukanId))
    }

    override fun onSortClicked(type: DukansSortType) {
        val newDirection = if (currentState.sort.type == type) {
            toggleSortDirection(currentState.sort.direction)
        } else {
            SortDirection.ASC
        }
        updateState {
            it.copy(
                sort = DukanManagementScreenState.SortState(
                    type = type,
                    direction = newDirection
                ),
                pageInfo = it.pageInfo.copy(page = 0)
            )
        }
        getDukans()
    }

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            else -> ErrorState.UnknownError
        }
    }

    private fun getDukansQueryParams(): DukanQueryParams {
        val trimmedQuery = currentState.query
            .trim().replace(Regex("\\s+"), " ").ifBlank { null }
        return DukanQueryParams(
            searchInput = trimmedQuery,
            sortType = currentState.sort.type,
            sortDirection = currentState.sort.direction,
            page = currentState.pageInfo.page,
            size = PAGE_SIZE,
            status = Dukan.Status.APPROVED,
            activationStatus = null,
        )
    }

    private fun getDukans() {
        tryToExecute(
            callee = { dukanRepository.getDukans(getDukansQueryParams()) },
            onSuccess = ::onSuccessGetDukans,
            onError = ::onFailureGetDukans,
            onStart = { updateState { it.copy(isLoading = true) } },
            onFinish = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatcher
        )
    }

    private fun onSuccessGetDukans(result: PagedResult<Dukan>) {
        dukans = result.items
        updateState {
            it.copy(
                dukans = result.items.mapIndexed { index, dukan ->
                    dukan.toUIState(
                        currentPage = result.currentPage,
                        indexInList = index,
                        itemCount = PAGE_SIZE
                    )
                },
                pageInfo = DukanManagementScreenState.PageInfo(
                    page = result.currentPage,
                    totalPages = result.totalPages
                ),
                totalDukans = result.totalElements,
                isLoading = false,
                errorState = null
            )
        }
    }

    private fun onFailureGetDukans(errorState: ErrorState) {
        updateState {
            it.copy(
                errorState = errorState,
                isLoading = false
            )
        }
    }

    private companion object {
        const val PAGE_SIZE = 8
        const val SEARCH_DEBOUNCE_DELAY = 500L
    }
}