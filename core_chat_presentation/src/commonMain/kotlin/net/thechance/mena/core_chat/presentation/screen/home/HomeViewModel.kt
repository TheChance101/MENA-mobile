package net.thechance.mena.core_chat.presentation.screen.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.WalletRoute
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.Paginator
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.getUuidOrNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class HomeViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
    effector: ChatEffector
) : BaseViewModel<HomeScreenState>(HomeScreenState(), effector) {

    private val paginator by lazy {
        Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::onLoadChatSummary,
            onRequest = ::getChatsSummary,
            getNextKey = { currentPage, _ -> currentPage + 1 },
            onError = ::onLoadChatsSummaryError,
            onSuccess = { result, newPage -> onLoadChatsSummarySuccess(result) },
            endReached = { _, result -> result.isEmpty() || result.size < PAGE_SIZE }
        )
    }

    init {
        loadChatsSummary()
    }

    fun loadChatsSummary() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun getUserId(): Uuid? {
        // TODO : I Need The Current LoggedIn UserId
        return getUuidOrNull("150c97c6-21b7-4e52-b12d-8bbff486022d")
    }

    private fun onLoadChatSummary(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading) }
    }

    private suspend fun getChatsSummary(page: Int): List<ChatSummary> {
        val userId = getUserId()
        return if (userId != null) {
            val chats = chatRepository.getChatSummary(page, userId)
            chats.data
        } else emptyList()
    }

    private fun onLoadChatsSummaryError(throwable: Throwable?) {
        updateState { it.copy(isLoading = false) }
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.something_went_wrong),
                message = UiText.DynamicString(value = throwable?.message.toString()),
            )
        )
    }

    private fun onLoadChatsSummarySuccess(items: List<ChatSummary>) {
        updateState { it.copy(chats = it.chats + items.map { chat -> chat.toUi() }) }
    }

    fun onNewChatClicked() {
        tryToExecute(
            onStart = { updateState { it.copy(isLoading = false) } },
            execute = { contactsRepository.getSyncStatus() },
            onSuccess = { isSynced ->
                updateState { it.copy(isSynced = isSynced, isLoading = false) }
                if (isSynced) {
                    navigate(ContactsRoute)
                } else {
                    navigate(SyncContactsRoute(forceSync = false))
                }
            }
        )
    }

    fun onWalletClicked() {
        navigate(WalletRoute)
    }

    companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_PAGE = 0
    }
}


