package net.thechance.mena.admin_panel.presentation.screen.users_management

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.model.PagedResult
import net.thechance.mena.admin_panel.domain.model.UserQueryParams
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.status_updated_title
import net.thechance.mena.admin_panel.resources.user_activated
import net.thechance.mena.admin_panel.resources.user_blocked
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class UsersManagementViewModel(
    @Provided private val userRepository: UserRepository,
    @Provided private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UsersManagementScreenState, Unit>(
    UsersManagementScreenState()
), UsersManagementInteractionListener {

    private var searchJob: Job? = null

    init {
        getUsers()
    }

    override fun onSortClicked(type: UsersManagementScreenState.SortType) {
        val newDirection = if (currentState.sort.type == type) {
            currentState.sort.direction.toggle()
        } else {
            UsersManagementScreenState.SortDirection.ASC
        }
        updateState {
            it.copy(
                sort = UsersManagementScreenState.SortState(
                    type = type,
                    direction = newDirection
                ),
                pageInfo = it.pageInfo.copy(page = 0)
            )
        }
        getUsers()
    }

    override fun onSearchQueryChanged(query: String) {
        if (query == currentState.query) return

        val trimmedQuery = query.trim().replace(Regex("\\s+"), " ")

        updateState { it.copy(query = query, pageInfo = it.pageInfo.copy(page = 0)) }
        searchJob?.cancel()

        if (trimmedQuery.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                getUsers()
            }
        } else {
            getUsers()
        }
    }

    override fun onClearQueryClicked() {
        onSearchQueryChanged("")
    }

    override fun onPageChanged(page: Int) {
        updateState { it.copy(pageInfo = it.pageInfo.copy(page = page)) }
        getUsers()
    }

    override fun onRetryClicked() {
        getUsers()
    }

    override fun onBlockDialogClicked(userId: Uuid) {
        updateState { it.copy(isBlockDialogShown = true, selectedUserId = userId) }
    }

    override fun onBlockDialogDismissed() {
        updateState { it.copy(isBlockDialogShown = false, selectedUserId = null) }
    }

    override fun onToggleUserStatusClicked(userId: Uuid, userStatus: User.Status) {
        when (userStatus) {
            User.Status.ACTIVE -> onBlockDialogClicked(userId)
            User.Status.BLOCKED -> updateUserStatus(userId, User.Status.ACTIVE)
        }
    }

    override fun onBlockConfirmed() {
        val userId = state.value.selectedUserId ?: return
        updateUserStatus(userId, User.Status.BLOCKED)
        onBlockDialogDismissed()
    }

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            else -> ErrorState.UnknownError
        }
    }

    private fun getUsers() {
        val queryParams = getUserQueryParams()

        tryToExecute(
            callee = { userRepository.getUsers(queryParams) },
            onSuccess = ::onGetUsersSuccess,
            onError = ::onError,
            onStart = ::onGetUsersStart,
            onFinish = ::onGetUsersFinish,
            dispatcher = dispatcher
        )
    }

    private fun onGetUsersStart() {
        updateState { it.copy(isLoading = true) }
    }

    private fun onGetUsersFinish() {
        updateState { it.copy(isLoading = false, isInitialLoading = false) }
    }

    private fun getUserQueryParams(): UserQueryParams {
        val trimmedQuery = currentState.query
            .trim().replace(Regex("\\s+"), " ").ifBlank { null }

        return UserQueryParams(
            searchInput = trimmedQuery,
            sortType = currentState.sort.type.toEntity(),
            sortDirection = currentState.sort.direction.toEntity(),
            page = currentState.pageInfo.page,
            size = PAGE_SIZE
        )
    }

    private fun onGetUsersSuccess(result: PagedResult<User>) {
        updateState {
            it.copy(
                users = result.items.mapIndexed { index, user ->
                    user.toUIState(
                        currentPage = result.currentPage,
                        indexInList = index
                    )
                },
                pageInfo = UsersManagementScreenState.UserPageInfo(
                    page = result.currentPage,
                    totalPages = result.totalPages
                ),
                errorState = null,
                totalUsers = result.totalElements
            )
        }
    }

    private suspend fun onError(errorState: ErrorState) {
        updateState { it.copy(errorState = errorState) }
        showSnackBar(
            title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
            message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
            isSuccess = false
        )
    }

    private fun updateUserStatus(userId: Uuid, newStatus: User.Status) {
        tryToExecute(
            callee = { userRepository.updateUserStatus(userId, newStatus) },
            onSuccess = { onUpdateUserStatusSuccess(userId, newStatus) },
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    private suspend fun onUpdateUserStatusSuccess(userId: Uuid, newStatus: User.Status) {
        updateState {
            it.copy(
                users = it.users.map { user ->
                    if (user.id == userId) user.copy(status = newStatus) else user
                }
            )
        }

        val message = when (newStatus) {
            User.Status.ACTIVE -> stringProvider.getString(Res.string.user_activated)
            User.Status.BLOCKED -> stringProvider.getString(Res.string.user_blocked)
        }
        showSnackBar(
            title = stringProvider.getString(Res.string.status_updated_title),
            message = message,
            isSuccess = true
        )
    }

    private suspend fun showSnackBar(
        title: String,
        message: String,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        if (state.value.snackBar.isVisible && state.value.snackBar.message == message) return
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

    private companion object {
        const val PAGE_SIZE = 8
        const val SEARCH_DEBOUNCE_DELAY = 500L
    }
}
