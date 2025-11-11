package net.thechance.mena.core_chat.presentation.screen.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_get_balance
import mena.core_chat_presentation.generated.resources.could_not_load_chats
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.no_internet
import mena.core_chat_presentation.generated.resources.no_internet_message
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.model.SyncState
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.Paginator
import net.thechance.mena.core_chat.presentation.utils.UiText
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
            onSuccess = { result, _ -> Unit },
            endReached = { _, result -> result.isLastPage })
    }

    init {
        getBalanceAmount()
        onChatsListScrolled()
        observeChatSummariesList()
        observeChatSummariesSyncState()
        messageRepository.initializeWebsocketConnection()
    }
    private fun observeChatSummariesList() {
        tryToCollect(collect = {
            chatRepository.observeChatSummaries()
        }, onCollect = { chatSummaries ->
            val updatedChatsSummaries =
                chatSummaries?.sortedByDescending { it.lastMessage?.sendAt }?.map { it.toUi() }
                    ?.distinctBy { it.id } ?: return@tryToCollect

            updateState {
                it.copy(
                    chats = mergedChats(
                        oldChats = it.chats, newChats = updatedChatsSummaries
                    )
                )

            }
        })
    }

    private fun mergedChats(
        oldChats: List<ChatUiState>, newChats: List<ChatUiState>
    ): List<HomeScreenState.ChatUiState> {
        if (newChats.isEmpty()) return emptyList()
        val newChatsMap = newChats.associateBy { it.id }.toMutableMap()
        val map = oldChats
            .filter { it.id in newChatsMap }
            .distinctBy { it.id }
            .associateBy { it.id }
            .toMutableMap()
        newChats.forEach { map[it.id] = it }
        return map.values.sortedByDescending {
            it.lastMessage?.time
        }
    }

    private fun observeChatSummariesSyncState() {
        tryToCollect(
            collect = {
                chatRepository.observeChatSummariesSyncState()
            }, onCollect = {
                if (it == null) return@tryToCollect
                when (it) {
                    is SyncState.ChatsSummariesSyncedSuccess -> Unit
                    is SyncState.DeletedChatsSyncedSuccess -> Unit
                    is SyncState.Error -> showErrorLoadingChatsSnackBar()
                    SyncState.Offline -> showNoInternetSnackBar()
                }
            }
        )
    }

    private fun getBalanceAmount() {
        tryToExecute(
            onStart = { updateState { it.copy(isBalanceLoading = true) } },
            execute = { balanceRepository.getBalance() },
            onSuccess = ::onGetBalanceAmountSuccess,
            onError = { onGetBalanceAmountError() })
    }

    private fun onGetBalanceAmountSuccess(balanceAmount: Double) {
        updateState {
            it.copy(
                balanceAmount = balanceAmount.toInt().toString(), isBalanceLoading = false
            )
        }
    }

    private fun onGetBalanceAmountError() {
        updateState { it.copy(isBalanceLoading = false, balanceAmount = "--") }
        showSnackBar(
            titleStringResource = Res.string.error,
            messageStringResource = Res.string.could_not_get_balance,
            isError = true
        )
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
            pageNumber = pageNumber, pageSize = PAGE_SIZE
        )
    }

    private fun onLoadChatsSummaryError() {
        showSnackBar(
            titleStringResource = Res.string.something_went_wrong,
            messageStringResource = Res.string.could_not_load_chats,
            isError = true
        )
    }

    private fun showNoInternetSnackBar() {
        showSnackBar(
            titleStringResource = Res.string.no_internet,
            messageStringResource = Res.string.no_internet_message,
            isError = true
        )
    }

    private fun showErrorLoadingChatsSnackBar() {
        showSnackBar(
            titleStringResource = Res.string.something_went_wrong,
            messageStringResource = Res.string.could_not_load_chats,
            isError = true
        )
    }


    override fun onNewChatClicked() {
        tryToExecute(
            execute = { contactsRepository.getHasUserSyncedContactsStatus() },
            onSuccess = ::onGetSyncStatusSuccess,
            onError = { onGetSyncStatusError() })
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
            execute = { chatRepository.disconnect() })
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


