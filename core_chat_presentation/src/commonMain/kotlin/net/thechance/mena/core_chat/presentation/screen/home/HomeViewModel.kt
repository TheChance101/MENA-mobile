package net.thechance.mena.core_chat.presentation.screen.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_load_chats
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
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
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.service.PrayerTimeService
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class HomeViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val balanceRepository: BalanceRepository,
    private val prayerTimeService: PrayerTimeService,
    private val locationService: LocationService,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<HomeScreenState, HomeScreenEffect>(HomeScreenState(), dispatcher),
    HomeScreenInteractionListener {

    private val _maxItemsState = MutableStateFlow(0)
    private val maxItemsState = _maxItemsState.asStateFlow()

    private val paginator by lazy {
        Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::changeLoadingState,
            onRequest = ::getChatsSummary,
            getNextKey = { currentPage, _ ->
                currentPage + 1
            },
            onError = { onLoadChatsSummaryError() },
            onSuccess = { result, _ ->
                onGetChatSummarySuccess(result.data)
            },
            endReached = { _, result -> result.isLastPage })
    }

    init {
        observeBalanceAmount()
        onChatsListScrolled()
        observeChatSummariesList()
        observeChatSummariesSyncState()
        messageRepository.initializeWebsocketConnection()
        getCurrentAddressInfo()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeChatSummariesList() {
        tryToCollect(collect = {
            maxItemsState.flatMapLatest {
                chatRepository.observeChatSummaries(it)
            }
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
    private fun observeBalanceAmount() {
        tryToCollect(
            onStart = { updateState { it.copy(isBalanceLoading = true) } },
            collect = { balanceRepository.observeBalance() },
            onCollect = ::onObserveBalanceAmountSuccess,
            onError = { onObserveBalanceAmountError() }
        )
    }

    private fun onObserveBalanceAmountSuccess(balanceAmount: Double?) {
        if (balanceAmount == null) {
            onObserveBalanceAmountError()
            return
        }
        updateState {
            it.copy(
                balanceAmount = balanceAmount.toInt().toString(),
                isBalanceLoading = false
            )
        }
    }

    private fun onObserveBalanceAmountError() {
        updateState { it.copy(isBalanceLoading = false, balanceAmount = "") }
    }

    override fun onChatsListScrolled() {
        viewModelScope.launch {
            _maxItemsState.update { it + PAGE_SIZE }
            paginator.loadNextItems()
        }
    }

    private fun changeLoadingState(isLoading: Boolean) {
        updateState { it.copy(isChatsLoading = isLoading) }
    }

    private suspend fun getChatsSummary(pageNumber: Int): PagedData<ChatSummary> {
        return chatRepository.getChatsSummary(
            pageNumber = pageNumber,
            pageSize = PAGE_SIZE
        )
    }

    private fun onGetChatSummarySuccess(chats: List<ChatSummary>) {
        updateState {
            it.copy(
                chats = mergePages(
                    oldChats = it.chats,
                    newChats = chats.map { chatItem -> chatItem.toUi() })
            )
        }

    }

    private fun mergePages(
        oldChats: List<ChatUiState>,
        newChats: List<ChatUiState>
    ): List<ChatUiState> {
        return (oldChats + newChats).distinctBy { it.id }
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


    private fun getCurrentAddressInfo(){
        tryToExecute(
            onStart = { updateState { it.copy(isPrayerTimeLoading = true) } },
            execute = { locationService.getActiveAddress() },
            onSuccess = ::observeNextPrayer
        )
    }
    private fun observeNextPrayer(address: Address?){
        if (address == null) return

        tryToCollect(
            collect = { prayerTimeService.getNextPrayer(address) },
            onCollect = ::onObserveNextPrayerSuccess,
            onError = { onObserveNextPrayerError() }
        )
    }

    private fun onObserveNextPrayerSuccess(prayerTime: PrayerTime?) {
        updateState { it.copy(prayerUiState = prayerTime?.toUi(), isPrayerTimeLoading = false) }
    }
    private fun onObserveNextPrayerError() {
        updateState { it.copy(prayerUiState = null, isPrayerTimeLoading = false) }
    }

    override fun onNewChatClicked() {
        tryToExecute(
            execute = { contactsRepository.getHasUserSyncedContactsStatus() },
            onSuccess = ::onGetSyncStatusSuccess,
            onError = { onGetSyncStatusError() })
    }

    private fun onGetSyncStatusSuccess(isSynced: Boolean) {
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


