package net.thechance.mena.admin_panel.presentation.screen.users_management

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import net.thechance.mena.admin_panel.domain.entity.User
import net.thechance.mena.admin_panel.domain.entity.User.UserState
import net.thechance.mena.admin_panel.domain.repository.UserRepo
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class UsersManagementViewModel(
    @Provided private val userRepo: UserRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<UsersManagementScreenState, UsersManagementEffect>
    (UsersManagementScreenState()), UsersManagementInteractionListener {

    init {
        getUsers()
    }


    /**
     * note: in callee we are using first() because we have a mock repo that uses StateFlow to emit data,
     * in real implementation we will use suspend function to get data automatically
     */
    private fun getUsers() {
        tryToExecute(
            callee = { userRepo.getAllUsers().first() },
            dispatcher = dispatcher,
            onSuccess = ::onGetUsersSuccess,
            onError = { onGetUsersError() }
        )
    }

    private fun onGetUsersSuccess(users: List<User>) {
        val userItems = users.map { it.toUIState() }
        updateState {
            it.copy(
                users = userItems,
                filteredUsers = userItems,
                isLoading = false,
                errorState = null
            )
        }
    }

    private fun onGetUsersError() {
        updateState {
            it.copy(
                isLoading = false,
                errorState = null
            )
        }
    }

    override fun onNavigateBackClicked() {
        sendEffect(UsersManagementEffect.NavigateBack)
    }

    override fun onBlockUserClicked(userId: Uuid) {
        updateState {
            it.copy(
                showBlockDialog = true,
                selectedUserId = userId
            )
        }
    }

    override fun onActivateUserClicked(userId: Uuid) {}

    override fun onRetryClicked() {
        getUsers()
    }

    override fun onStatusClick(userId: Uuid) {
        val user = state.value.filteredUsers.find { it.id == userId }

        when (user?.userStates) {
            UserState.ACTIVE -> onShowBlockDialog(userId)
            UserState.BLOCKED -> onActivateUserClicked(userId)
            else -> {}
        }
    }

    override fun onSortUsersNameClicked() {}

    override fun onSortLastLoginDateClicked() {}

    override fun onSortLastVisitDateClicked() {}

    override fun onSearchQueryChanged(query: String) {}

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

    override fun onConfirmBlock() {}
}