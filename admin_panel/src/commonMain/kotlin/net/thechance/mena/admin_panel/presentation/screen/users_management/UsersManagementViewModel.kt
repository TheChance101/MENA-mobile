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
) : BaseViewModel<UsersManagementScreenState, Unit>(
    UsersManagementScreenState()
), UsersManagementInteractionListener {

    init {
        getUsers()
    }

    private fun getUsers() {
        val queryParams = getUserQueryParams()

        tryToExecute(
            callee = { userRepository.getUsers(queryParams) },
            onSuccess = ::onGetUsersSuccess,
            onError = ::onError,
            onStart = { updateState { it.copy(isLoading = true) } },
            dispatcher = dispatcher
        )
    }

    private fun getUserQueryParams(): UserQueryParams {
        return UserQueryParams(
            searchInput = currentState.query.ifBlank { null },
            sortType = currentState.sort.type.toEntity(),
            sortDirection = currentState.sort.direction.toEntity(),
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

    private fun onError(errorState: ErrorState) {
        updateState { it.copy(isLoading = false, errorState = errorState) }
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
                )
            )
        }
        getUsers()
    }

    override fun onSearchQueryChanged(query: String) {
        updateState { it.copy(query = query) }
        getUsers()
    }

    override fun onRetryClicked() {
        getUsers()
    }

    override fun showBlockDialog(userId: Uuid) {
        updateState { it.copy(showBlockDialog = true, selectedUserId = userId) }
    }

    override fun onDismissBlockDialog() {
        updateState { it.copy(showBlockDialog = false, selectedUserId = null) }
    }

    override fun onToggleUserStatusClicked(userId: Uuid) {
        val user = state.value.users.find { it.id == userId } ?: return

        when (user.status) {
            Status.ACTIVE -> showBlockDialog(userId)
            Status.BLOCKED -> updateUserStatus(userId, Status.ACTIVE)
        }
    }

    override fun onConfirmBlock() {
        val userId = state.value.selectedUserId ?: return
        updateUserStatus(userId, Status.BLOCKED)
        onDismissBlockDialog()
    }

    private fun updateUserStatus(userId: Uuid, newStatus: Status) {
        tryToExecute(
            callee = { userRepository.updateUserStatus(userId, newStatus) },
            onSuccess = {onUpdateUserStatusSuccess(userId, newStatus)},
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    private fun onUpdateUserStatusSuccess(userId: Uuid, newStatus: Status) {
        updateState {
            it.copy(
                users = it.users.map { user ->
                    if (user.id == userId) user.copy(status = newStatus) else user
                }
            )
        }
    }

    private companion object {
        const val PAGE = 0
        const val SIZE = 8
    }
}
