package net.thechance.mena.core_chat.presentation.screen.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_load_chats
import mena.core_chat_presentation.generated.resources.could_not_sync_contacts_message
import mena.core_chat_presentation.generated.resources.no_internet
import mena.core_chat_presentation.generated.resources.no_internet_message
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.WeatherDetails
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.model.SyncState
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.domain.repository.WeatherRepository
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
    private val weatherRepository: WeatherRepository,
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
        observeBalanceAmount()
        onChatsListScrolled()
        listenToIncomingMessages()
        listenToMarkAsReadEvent()
        observeDeleteChat()
        observeChatSummariesSyncState()
        getCurrentAddressInfo()
    }

    private fun observeChatSummariesSyncState() {
        tryToCollect(
            collect = { chatRepository.observeChatSummariesSyncState() },
            onCollect = ::onCollectSyncString
        )
    }

    private suspend fun onCollectSyncString(syncState: SyncState) {
        delay(100)
        when (syncState) {
            is SyncState.Error -> showErrorLoadingChatsSnackBar()
            is SyncState.Offline -> showNoInternetSnackBar()
            is SyncState.ChatsSummariesSynced -> onChatsSummariesSynced(syncState)
            is SyncState.DeletedChatsSynced -> {
                updateState { it.copy(chats = it.chats.filterNot { syncState.chatIds.contains(it.id) }) }
            }
        }
    }

    private fun onChatsSummariesSynced(syncState: SyncState.ChatsSummariesSynced) {
        updateState { state ->
            state.copy(
                chats = syncState.chatSummaries
                    .map { chatSummary -> chatSummary.toUi() }
                    .let { chatSummaries -> chatSummaries + state.chats }
                    .distinctBy { it.id }
                    .sortedByDescending { chatSummary -> chatSummary.lastMessage?.time }
            )
        }
    }

    private fun listenToMarkAsReadEvent() {
        tryToCollect(
            collect = { messageRepository.observeReadMessages() },
            onCollect = ::onCollectMarkAsReadEvent,
        )
    }

    private fun observeDeleteChat() {
        tryToCollect(
            collect = { messageRepository.observeDeleteChat() },
            onCollect = ::onCollectDeleteChatEvent
        )
    }

    private fun onCollectDeleteChatEvent(deleteChatEvent: DeleteChatEvent) {
        updateState {
            it.copy(
                chats = it.chats.filterNot { chat -> chat.id == deleteChatEvent.chatId }
            )
        }
    }

    private suspend fun onCollectMarkAsReadEvent(markMessageAsReadEvent: MarkMessageAsReadEvent) {
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
            onCollect = ::onCollectMessage
        )
    }

    private suspend fun onCollectMessage(message: Message) {
        val updatedChatSummary = chatRepository.getChatSummaryById(message.chatId).toUi()

        val updatedChats = listOf(updatedChatSummary) +
                state.value.chats
                    .filterNot { it.id == message.chatId }

        updateState { it.copy(chats = updatedChats.distinctBy { it.id }) }
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
                balanceAmount = balanceAmount.toString(),
                isBalanceLoading = false
            )
        }
    }

    private fun onObserveBalanceAmountError() {
        updateState { it.copy(isBalanceLoading = false, balanceAmount = "") }
    }

    override fun onChatsListScrolled() {
        viewModelScope.launch {
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

    private fun onLoadChatsSummarySuccess(items: PagedData<ChatSummary>) {
        val chats = state.value.chats + items.data.map { chat -> chat.toUi() }

        updateState {
            it.copy(
                chats = chats
                    .distinctBy { it.id }
                    .sortedByDescending { it.lastMessage?.time }
            )
        }
    }

    private fun getCurrentAddressInfo() {
        tryToExecute(
            onStart = {
                updateState { it.copy(isPrayerTimeLoading = true, isWeatherLoading = true) }
            },
            execute = { locationService.getActiveAddress() },
            onSuccess = ::onGetCurrentAddressSuccess,
            onError = { onGetCurrentAddressError() }
        )
    }

    private fun onGetCurrentAddressSuccess(address: Address?) {
        observeNextPrayer(address)
        getWeatherDetails(address)
    }

    private fun onGetCurrentAddressError() {
        updateState { it.copy(isPrayerTimeLoading = false, isWeatherLoading = false) }
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

    private fun getWeatherDetails(address: Address?) {
        if (address == null) return

        tryToExecute(
            execute = {
                weatherRepository.getWeatherDetails(
                latitude = address.latitude,
                longitude = address.longitude
                )
            },
            onSuccess = ::onGetWeatherDetailsSuccess,
            onError = { onGetWeatherDetailsError() }
        )
    }

    private fun onGetWeatherDetailsSuccess(weatherDetails: WeatherDetails) {
        updateState { it.copy(weatherUiState = weatherDetails.toUi(), isWeatherLoading = false) }
    }

    private fun onGetWeatherDetailsError() {
        updateState { it.copy(weatherUiState = null, isWeatherLoading = false) }
    }

    override fun onNewChatClicked() {
        tryToExecute(
            execute = { contactsRepository.getHasUserSyncedContactsStatus() },
            onSuccess = ::onGetSyncStatusSuccess,
            onError = { onGetSyncStatusError() }
        )
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


