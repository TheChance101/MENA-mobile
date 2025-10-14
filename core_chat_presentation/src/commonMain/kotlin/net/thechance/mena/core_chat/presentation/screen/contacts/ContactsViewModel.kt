@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contact_not_mena_user
import mena.core_chat_presentation.generated.resources.could_not_load_the_contacts
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.screen.syncContacts.IS_SYNC_SUCCESS
import net.thechance.mena.core_chat.presentation.shared.BasePagingSource
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.UiText
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ContactsViewModel(
    private val contactsRepository: ContactsRepository,
    private val chatRepository: ChatRepository,
    effector: ChatEffector,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ContactsScreenState>(ContactsScreenState(), effector, dispatcher),
    ContactsScreenInteractionListener {

    init {
        observeSyncSuccess()
        loadContacts()
    }

    private fun observeSyncSuccess() {
        tryToCollect(
            collect = { popBackStackArgsFlow },
            onCollect = { args ->
                val success = args?.get(IS_SYNC_SUCCESS) as? Boolean ?: return@tryToCollect
                if (success) {
                    onRefreshContactsClicked()
                    setNavigationArgs(IS_SYNC_SUCCESS to false)
                }
            }
        )
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
        popBackStack()
    }

    override fun onReSyncClicked() {
        navigate(SyncContactsRoute(forceSync = true))
    }

    override fun onContactClicked(contactId: Uuid?) {
        if (contactId == null) {
            showSnackBar(
                SnackBarData(
                    title = UiText.StringRes(Res.string.something_went_wrong),
                    message = UiText.StringRes(Res.string.contact_not_mena_user),
                )
            )
            return
        }
        navigateToChatByContactId(contactId)
    }

    private fun navigateToChatByContactId(contactId: Uuid) {
        tryToExecute(
            execute = { chatRepository.getChatByContactUserId(contactId) },
            onSuccess = ::onContactClickSuccess,
            onError = { onContactClickError() },
        )
    }

    private fun onContactClickSuccess(chat: Chat?) {
        navigate(ChatDetailsRoute(chatId = chat?.id.toString() , chat?.name.orEmpty()))
    }

    private fun onContactClickError() {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.something_went_wrong),
                message = UiText.StringRes(Res.string.contact_not_mena_user),
            )
        )
    }

    private fun onDataLoadError() {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.something_went_wrong),
                message = UiText.StringRes(Res.string.could_not_load_the_contacts),
            )
        )
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