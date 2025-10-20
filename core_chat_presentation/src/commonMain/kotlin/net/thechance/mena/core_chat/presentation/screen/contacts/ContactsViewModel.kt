@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.shared.BasePagingSource
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ContactsViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ContactsScreenState, ContactsScreenEffect>(ContactsScreenState(), dispatcher),
    ContactsScreenInteractionListener {

    init {
        loadContacts()
    }

    fun onSyncSuccess() {
        onRefreshContactsClicked()
    }

    private fun loadContacts() {
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
        updateState { it.copy(contacts = flowOf(pagingData ?: PagingData.empty())) }
    }

    override fun onRefreshContactsClicked() {
        loadContacts()
    }

    override fun onBackClicked() {
        emitEffect(ContactsScreenEffect.NavigateBack)
    }

    override fun onReSyncClicked() {
        emitEffect(ContactsScreenEffect.NavigateToSyncContacts)
    }

    override fun onContactClicked(contactUserId: Uuid?) {
        if (contactUserId == null) {
//            showSnackBar(
//                SnackBarData(
//                    title = UiText.StringRes(Res.string.something_went_wrong),
//                    message = UiText.StringRes(Res.string.contact_not_mena_user),
//                )
//            )
            return
        }
        navigateToChatByUserId(contactUserId)
    }

    private fun navigateToChatByUserId(contactId: Uuid) {
        tryToExecute(
            execute = { chatRepository.getChatByContactUserId(contactId) },
            onSuccess = ::onContactClickSuccess,
            onError = { onContactClickError() },
        )
    }

    private fun onContactClickSuccess(chat: Chat) {
        emitEffect(
            ContactsScreenEffect.NavigateToChat(
                chatId = chat.id.toString(),
                chat.name
            )
        )
    }

    private fun onContactClickError() {
//        showSnackBar(
//            SnackBarData(
//                title = UiText.StringRes(Res.string.something_went_wrong),
//                message = UiText.StringRes(Res.string.contact_not_mena_user),
//            )
//        )
    }

    private fun onDataLoadError() {
//        showSnackBar(
//            SnackBarData(
//                title = UiText.StringRes(Res.string.something_went_wrong),
//                message = UiText.StringRes(Res.string.could_not_load_the_contacts),
//            )
//        )
    }

    private fun createContactsPagingSource(onError: ((ChatException) -> Unit)? = { onDataLoadError() })
            : PagingSource<Int, Contact> {
        return BasePagingSource(
            onError = onError,
            fetchItems = { page ->
                contactsRepository.getUserContacts(page)
            }
        )
    }
}