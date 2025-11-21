@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.contact_not_mena_user
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.no_internet
import mena.core_chat_presentation.generated.resources.no_internet_connected
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.exception.NoInternetException
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.shared.BasePagingSource
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.PhoneNumberFormatterUtil
import net.thechance.mena.core_chat.presentation.utils.UiText
import org.jetbrains.compose.resources.StringResource
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
            onError = ::onOperationFailed
        )
    }

    private fun loadContactsOperation(): Flow<PagingData<ContactUiState>> {
        return createPagingFlow(
            pagingSourceFactory = { createContactsPagingSource() },
            mapper = Contact::toUi
        )
    }

    private fun onLoadContactsSuccess(pagingData: PagingData<ContactUiState>) {
        val contacts = pagingData.map {
            it.copy(
                phoneNumber =
                    runCatching { PhoneNumberFormatterUtil.format(it.phoneNumber) }.getOrNull()
                        ?: it.phoneNumber
            )
        }
        updateState { it.copy(contacts = flowOf(contacts)) }
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
            showSnackBar(
                titleStringResource = Res.string.something_went_wrong,
                messageStringResource = Res.string.contact_not_mena_user,
                isError = true
            )
            return
        }
        navigateToChatByUserId(contactUserId)
    }

    private fun navigateToChatByUserId(userId: Uuid) {
        tryToExecute(
            execute = { chatRepository.getChatByOtherUserId(userId) },
            onSuccess = ::onContactClickSuccess,
            onError = ::onOperationFailed,
        )
    }

    private fun onContactClickSuccess(chat: Chat) {
        emitEffect(
            ContactsScreenEffect.NavigateToChat(
                chatId = chat.id.toString(),
                chatName = chat.name
            )
        )
    }

    private fun onOperationFailed(e: Throwable) {
        when (e) {
            is NoInternetException -> {
                showSnackBar(
                    titleStringResource = Res.string.no_internet,
                    messageStringResource = Res.string.no_internet_connected,
                    isError = true
                )
            }

            else -> showSnackBar(
                titleStringResource = Res.string.error,
                messageStringResource = Res.string.something_went_wrong,
                isError = true
            )
        }
    }

    private fun showSnackBar(
        titleStringResource: StringResource,
        messageStringResource: StringResource,
        isError: Boolean = false
    ) {
        emitEffect(
            ContactsScreenEffect.ShowSnackBar(
                SnackBarData(
                    title = UiText.StringRes(titleStringResource),
                    message = UiText.StringRes(messageStringResource),
                    isError = isError
                )
            )
        )
    }

    private fun createContactsPagingSource(onError: ((ChatException) -> Unit)? = ::onOperationFailed)
            : PagingSource<Int, Contact> {
        return BasePagingSource(
            onError = onError,
            fetchItems = { page ->
                contactsRepository.getUserContacts(page)
            }
        )
    }
}