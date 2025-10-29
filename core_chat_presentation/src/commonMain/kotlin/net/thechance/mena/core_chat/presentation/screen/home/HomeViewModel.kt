package net.thechance.mena.core_chat.presentation.screen.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_load_chats
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.Paginator
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.getFormattedTimeWithTodayTimeOrYesterdayTextOrSimpleDate
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class HomeViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val balanceRepository: BalanceRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<HomeScreenState, HomeScreenEffect>(HomeScreenState(), dispatcher),
    HomeScreenInteractionListener {

    private val paginator by lazy {
        Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::changeLoadingState,
            onRequest = ::getChatsSummary,
            getNextKey = { currentPage, _ -> currentPage + 1 },
            onError = { onLoadChatsSummaryError() },
            onSuccess = { result, _ -> onLoadChatsSummarySuccess(result) },
            endReached = { _, result -> result.isLastPage }
        )
    }

    init {
        getBalanceAmount()
        onChatsListScrolled()
        listenToIncomingMessages()
        listenToMarkAsReadEvent()
    }

    private fun listenToMarkAsReadEvent() {
        tryToCollect(
            collect = { messageRepository.observeReadMessages() },
            onCollect = ::onCollectMarkAsReadEvent,
        )
    }

    private suspend fun onCollectMarkAsReadEvent(markMessageAsReadEvent: MarkMessageAsReadEvent?) {
        if (markMessageAsReadEvent == null) return
        if (markMessageAsReadEvent.readByMe.not()) return

        val newChatSummary = chatRepository.getChatSummaryById(markMessageAsReadEvent.chatId).toUi()
        updateState {
            it.copy(
                chats = it.chats.map { chatSummary ->
                    if (chatSummary.id == newChatSummary.id) newChatSummary
                    else chatSummary
                }
            )
        }
    }

    private fun listenToIncomingMessages() {
        tryToCollect(
            collect = { messageRepository.observeMessagesForChatOrAll() },
            onCollect = ::onCollectMessage,
            onError = { },
        )
    }

    private suspend fun onCollectMessage(message: Message?) {
        if (message == null) return
        val chatSummary = state.value.chats.firstOrNull { chat ->
            chat.id == message.chatId
        }

        if (chatSummary == null) {
            val newChatSummary = chatRepository.getChatSummaryById(message.chatId).toUi()
            updateState {
                it.copy(
                    chats = listOf(newChatSummary) + it.chats
                )
            }
            return
        }

        val updatedChatSummary = chatSummary.copy(
            lastMessage = ChatUiState.MessageUiState(
                text = (message.content as MessageContent.Text).text,
                time = getFormattedTimeWithTodayTimeOrYesterdayTextOrSimpleDate(message.sendAt),
                isMine = message.isMine,
            ),
            status =
                if (message.isMine) ChatUiState.Status.Sent
                else ChatUiState.Status.UnRead(
                    if (chatSummary.status is ChatUiState.Status.UnRead) chatSummary.status.count + 1
                    else 1
                )
        )

        val updatedChats =
            listOf(updatedChatSummary) +
                    state.value.chats.filterNot { it.id == message.chatId }

        updateState { it.copy(chats = updatedChats.distinctBy { it.id }) }
    }

    private fun getBalanceAmount() {
        tryToExecute(
            execute = { balanceRepository.getBalance() },
            onSuccess = ::onGetBalanceAmountSuccess
        )
    }

    private fun onGetBalanceAmountSuccess(balanceAmount: Double) {
        val balance = (balanceAmount * 100).toInt() / 100.0
        updateState { it.copy(balanceAmount = balance) }
    }

    override fun onChatsListScrolled() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun changeLoadingState(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading) }
    }

    private suspend fun getChatsSummary(pageNumber: Int): PagedData<ChatSummary> {
        return chatRepository.getChatsSummary(
            pageNumber = pageNumber,
            pageSize = PAGE_SIZE
        )
    }

    private fun onLoadChatsSummaryError() {
        showSnackBar(
            titleStringResource = Res.string.something_went_wrong,
            messageStringResource = Res.string.could_not_load_chats,
            isError = true
        )
    }

    private fun onLoadChatsSummarySuccess(items: PagedData<ChatSummary>) {
        val chats = items.data
            .sortedByDescending { it.lastMessage?.sendAt }
            .map { chat -> chat.toUi() }

        updateState { it.copy(chats = it.chats + chats) }
    }

    override fun onNewChatClicked() {
        tryToExecute(
            execute = { contactsRepository.getHasUserSyncedContactsStatus() },
            onSuccess = ::onGetSyncStatusSuccess,
            onError = { onGetSyncStatusError() }
        )
    }

    private fun onGetSyncStatusSuccess(isSynced: Boolean) {
        updateState { it.copy(isSynced = isSynced) }
        if (isSynced) {
            emitEffect(HomeScreenEffect.NavigateToContacts)
        } else {
            emitEffect(HomeScreenEffect.NavigateToSyncContacts)
        }
    }

    private fun onGetSyncStatusError() {
        showSnackBar(
            titleStringResource = Res.string.something_went_wrong,
            messageStringResource = Res.string.could_not_sync_contacts_message,
            isError = true
        )
    }

    override fun onChatClicked(chat: ChatUiState) {
        emitEffect(
            HomeScreenEffect.NavigateToChat(
                chatId = chat.id.toString(),
                chatName = chat.name,
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        tryToExecute(
            coroutineScope = CoroutineScope(Dispatchers.IO),
            execute = { chatRepository.disconnect() }
        )
    }

    override fun onWalletClicked() {
        emitEffect(HomeScreenEffect.NavigateToWallet)
    }

    private fun showSnackBar(
        titleStringResource: StringResource,
        messageStringResource: StringResource,
        isError: Boolean = false
    ) {
        emitEffect(
            HomeScreenEffect.ShowSnackBar(
                SnackBarData(
                    title = UiText.StringRes(titleStringResource),
                    message = UiText.StringRes(messageStringResource),
                    isError = isError
                )
            )
        )
    }

    companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_PAGE = 0
    }
}


