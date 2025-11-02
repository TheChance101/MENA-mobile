package net.thechance.mena.admin_panel.presentation.screen.users_management

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import net.thechance.mena.admin_panel.domain.entity.user.Status
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.model.UserQueryParams
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class UsersManagementViewModel(
    @Provided private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UsersManagementScreenState, UsersManagementEffect>(
    UsersManagementScreenState()
), UsersManagementInteractionListener {

    init {
        getUsers()
    }

    private fun getUsers() {
        val queryParams = userQueryParams()

        tryToExecute(
            callee = { userRepository.getUsers(queryParams) },
            onSuccess = ::onGetUsersSuccess,
            onError = ::onGetUsersError,
            onStart = { updateState { it.copy(isLoading = true) } },
            dispatcher = dispatcher
        )
    }

    private fun userQueryParams(): UserQueryParams {
        val currentState = state.value
        return UserQueryParams(
            searchInput = currentState.query.ifBlank { null },
            sortType = currentState.getActiveSortType(),
            sortDirection = currentState.getActiveSortDirection(),
            page = PAGE,
            size = SIZE
        )
    }

    private fun onGetUsersSuccess(users: List<User>) {
        updateState {
            it.copy(
                users = users.map(User::toUIState),
                isLoading = false,
                errorState = null
            )
        }
    }

    private fun onGetUsersError(errorState: ErrorState) {
        updateState {
            it.copy(
                isLoading = false,
                errorState = errorState
            )
        }
    }

    override fun onSortUsersNameClicked() {
        updateSortState { state ->
            state.copy(
                userNameSort = state.userNameSort.toggle(),
                lastLoginDateSort = UsersManagementScreenState.Sort.NONE,
                lastVisitDateSort = UsersManagementScreenState.Sort.NONE
            )
        }
    }

    override fun onSortLastLoginDateClicked() {
        updateSortState { state ->
            state.copy(
                userNameSort = UsersManagementScreenState.Sort.NONE,
                lastLoginDateSort = state.lastLoginDateSort.toggle(),
                lastVisitDateSort = UsersManagementScreenState.Sort.NONE
            )
        }
    }

    override fun onSortLastVisitDateClicked() {
        updateSortState { state ->
            state.copy(
                userNameSort = UsersManagementScreenState.Sort.NONE,
                lastLoginDateSort = UsersManagementScreenState.Sort.NONE,
                lastVisitDateSort = state.lastVisitDateSort.toggle()
            )
        }
    }

    private fun updateSortState(update: (UsersManagementScreenState) -> UsersManagementScreenState) {
        updateState(update)
        getUsers()
    }

    override fun onSearchQueryChanged(query: String) {
        updateState { it.copy(query = query) }
        getUsers()
    }

    override fun onRetryClicked() {
        getUsers()
    }

    override fun onShowBlockDialog(userId: Uuid) {
        updateState {
            it.copy(
                showBlockDialog = true,
                selectedUserId = userId
            )
        }
    }

    override fun onDismissBlockDialog() {
        updateState {
            it.copy(
                showBlockDialog = false,
                selectedUserId = null
            )
        }
    }

    override fun onToggleUserStatusClicked(userId: Uuid) {
        val user = state.value.users.find { it.id == userId } ?: return

        if (user.status == Status.ACTIVE) {
            onShowBlockDialog(userId)
        } else {
            activateUser(userId)
        }
    }


    override fun onConfirmBlock() {
        val userId = state.value.selectedUserId ?: return
        blockUser(userId)
        onDismissBlockDialog()
    }

    private fun blockUser(userId: Uuid) {
        tryToExecute(
            callee = { userRepository.updateUserStatus(userId, Status.BLOCKED) },
            onSuccess = {
                updateUserStatusInState(userId, Status.BLOCKED)
            },
            onError = ::onGetUsersError,
            dispatcher = dispatcher
        )
    }

    private fun activateUser(userId: Uuid) {
        tryToExecute(
            callee = { userRepository.updateUserStatus(userId, Status.ACTIVE) },
            onSuccess = {
                updateUserStatusInState(userId, Status.ACTIVE)
            },
            onError = ::onGetUsersError,
            dispatcher = dispatcher
        )
    }

    private fun updateUserStatusInState(userId: Uuid, newStatus: Status) {
        updateState { currentState ->
            currentState.copy(
                users = currentState.users.map { user ->
                    if (user.id == userId) {
                        user.copy(status = newStatus)
                    } else {
                        user
                    }
                }
            )
        }
    }
    private companion object {
        const val PAGE = 1
        const val SIZE = 20

    }
}
