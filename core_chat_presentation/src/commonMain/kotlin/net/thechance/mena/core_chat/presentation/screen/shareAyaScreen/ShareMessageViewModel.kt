package net.thechance.mena.core_chat.presentation.screen.shareAyaScreen

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.cannot_get_chat_for_that_contact
import mena.core_chat_presentation.generated.resources.could_not_load_the_contacts
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactUiState
import net.thechance.mena.core_chat.presentation.screen.contacts.toUi
import net.thechance.mena.core_chat.presentation.shared.BasePagingSource
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.UiText
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ShareMessageViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
) : BaseViewModel<ShareMessageScreenState, ShareMessageEffect>(ShareMessageScreenState()),
    ShareMessageInteractionListener {

    val searchQueryFlow = MutableStateFlow("")

    init {
        getContacts()
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(DEBOUNCE_TIME)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collectLatest {
                    getContacts()
                }
        }

    }

    private fun getContacts() {
        tryToCollect(
            collect = ::loadContactsOperation,
            onCollect = ::onLoadContactsSuccess,
            onError = { onDataLoadError() }
        )
    }

    private fun loadContactsOperation(): Flow<PagingData<ContactUiState>> {
        return createPagingFlow(
            pagingSourceFactory = { createContactsPagingSource() },
            mapper = Contact::toUi
        )
    }

    private fun onLoadContactsSuccess(pagingData: PagingData<ContactUiState>?) {
        updateState {
            it.copy(contacts = flowOf(pagingData?.filter { it.isMenaUser } ?: PagingData.empty()))
        }
    }

    private fun createContactsPagingSource(onError: ((ChatException) -> Unit)? = { onDataLoadError() })
            : PagingSource<Int, Contact> {
        return BasePagingSource(
            onError = onError,
            fetchItems = { page ->
                if (state.value.searchQuery.isEmpty())
                    contactsRepository.getUserContacts(page)
                else
                    contactsRepository.getContactsByName(name = state.value.searchQuery, pageNumber = page, isMenaUser = true)
            }
        )
    }

    private fun onDataLoadError() {
        showSnackBar(
            titleStringResource = Res.string.something_went_wrong,
            messageStringResource = Res.string.could_not_load_the_contacts,
            isError = true
        )
    }

    private fun showSnackBar(
        titleStringResource: StringResource,
        messageStringResource: StringResource,
        isError: Boolean = false
    ) {
        emitEffect(
            ShareMessageEffect.ShowSnackBar(
                SnackBarData(
                    title = UiText.StringRes(titleStringResource),
                    message = UiText.StringRes(messageStringResource),
                    isError = isError
                )
            )
        )
    }

    override fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            updateState { it.copy(searchQuery = query) }
            searchQueryFlow.value = query
            if (state.value.searchQuery.isEmpty()) onClearQueryClicked()
        }
    }

    override fun onContactClicked(contactId: Uuid?) {
        if (contactId == null)
            onContactClickError()
        else
            tryToExecute(
                execute = { chatRepository.getChatByOtherUserId(userId = contactId) },
                onSuccess = { chat ->
                    onContactClickSuccess(
                        chatId = chat.id,
                        chatName = chat.name
                    )
                },
                onError = { onContactClickError() }
            )
    }

    override fun onBackClicked() {
        emitEffect(effect = ShareMessageEffect.NavigateBack)
    }

    override fun onClearQueryClicked() {
        updateContacts("")
        searchQueryFlow.value = ""
    }

    private fun updateContacts(searchQuery: String) {
        updateState { it.copy(searchQuery = "") }
        getContacts()
    }

    private fun onContactClickError() {
        showSnackBar(
            titleStringResource = Res.string.something_went_wrong,
            messageStringResource = Res.string.cannot_get_chat_for_that_contact,
            isError = true
        )
    }

    private fun onContactClickSuccess(chatId: Uuid, chatName: String) {
        emitEffect(ShareMessageEffect.NavigateToChatScreen(chatId = chatId, chatName = chatName))
    }

    companion object {
        const val DEBOUNCE_TIME = 500L
    }
}