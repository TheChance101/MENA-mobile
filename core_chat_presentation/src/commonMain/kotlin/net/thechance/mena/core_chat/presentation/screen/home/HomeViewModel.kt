package net.thechance.mena.core_chat.presentation.screen.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.model.PagedData
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
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class HomeViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
    private val balanceRepository: BalanceRepository,
    effector: ChatEffector
) : BaseViewModel<HomeScreenState>(HomeScreenState(), effector), HomeScreenInteractionListener {

    private val paginator by lazy {
        Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::changeLoadingState,
            onRequest = ::getChatsSummary,
            getNextKey = { currentPage, _ -> currentPage + 1 },
            onError = ::onLoadChatsSummaryError,
            onSuccess = { result, newPage -> onLoadChatsSummarySuccess(result) },
            endReached = { _, result -> result.isLastPage }
        )
    }

    init {
        getBalanceAmount()
        onChatsListScrolled()
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

    private fun onLoadChatsSummaryError(throwable: Throwable?) {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.something_went_wrong),
                message = UiText.DynamicString(value = throwable?.message.toString()),
            )
        )
    }

    private fun onLoadChatsSummarySuccess(items: PagedData<ChatSummary>) {
        val chats = items.data
            .sortedByDescending { it.lastMessage.sendAt }
            .map { chat -> chat.toUi() }

        updateState { it.copy(chats = it.chats + chats) }
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
            },
            onError = ::onGetSyncStatusError
        )
    }

    private fun onGetSyncStatusError(throwable: Throwable?) {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.something_went_wrong),
                message = UiText.StringRes(Res.string.could_not_sync_contacts_message),
            )
        )
    }

    override fun onChatClicked(chat: ChatUiState) {
        navigate(
            ChatDetailsRoute(
                chatId = chat.id.toString(),
                chatName = chat.name,
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


