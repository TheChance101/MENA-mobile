package net.thechance.mena.core_chat.presentation.screen.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.ContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.navigation.WalletRoute
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.Paginator
import net.thechance.mena.core_chat.presentation.utils.UiText
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class HomeViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
    effector: ChatEffector
) : BaseViewModel<HomeScreenState>(HomeScreenState(), effector), HomeScreenInteractionListener {

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

    private fun onLoadChatSummary(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading) }
    }

    private suspend fun getChatsSummary(page: Int): List<ChatSummary> {
        return chatRepository.getChatSummary(page).data
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

    override fun onNewChatClicked() {
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

    override fun onChatClicked(chat: ChatUiState) {
        navigate(
            ChatDetailsRoute(
                chatId = chat.id.toString(),
                chatName = chat.name,
                chatImageUrl = chat.imageUrl.toString(),
                chatRequesterId = chat.id.toString() // TODO("Replace with the actual requester id")
            )
        )
    }

    override fun onWalletClicked() {
        navigate(WalletRoute)
    }

    companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_PAGE = 0
    }
}


