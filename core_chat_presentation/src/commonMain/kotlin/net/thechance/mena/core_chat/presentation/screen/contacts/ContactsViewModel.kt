package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.shared.BasePagingSource
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel

class ContactsViewModel(
    private val contactsRepository: ContactsRepository,
) : BaseViewModel<ContactsScreenState, ContactsScreenEffect>(ContactsScreenState()),
    ContactsScreenInteractionListener {

    init {
        loadContacts()
    }
    private fun loadContacts() {
        tryToCollect(
            collect = ::loadContactsOperation,
            onCollect = ::onLoadContactsSuccess,
            onError = ::onDataLoadError
        )
    }

    private fun loadContactsOperation(): Flow<PagingData<ContactUi>> {
        return createPagingFlow(
            pagingSourceFactory = { createContactsPagingSource() },
            mapper = Contact::toUiModel
        )
    }

    private fun onLoadContactsSuccess(pagingData: PagingData<ContactUi>?) {
        updateState { it.copy(contacts = flowOf(pagingData ?: PagingData.empty())) }
    }

    override fun onRefreshContacts() {
        loadContacts()
    }

    override fun onBackClick() {
        emitEffect(ContactsScreenEffect.NavigateBack)
    }

    override fun onResyncClick() {
        emitEffect(ContactsScreenEffect.NavigateToSyncContacts)
    }

    override fun onSnackBarDismiss() {
        updateState { it.copy(snackBarData = null) }
    }

    override fun onContactClick(contactId: Int) {
        emitEffect(ContactsScreenEffect.NavigateToChatScreen(contactId))
    }

    private fun onDataLoadError(e: Throwable) {
        updateState {
            it.copy(
                snackBarData = SnackBarData(
                    title = "Something went wrong",
                    message = e.message ?: "Unknown error",
                )
            )
        }
    }

    private fun createContactsPagingSource(onError: ((ChatException) -> Unit)? = ::onDataLoadError)
            : PagingSource<Int, Contact> {
        return BasePagingSource(
            onError = onError,
            fetchItems = { page, pageSize ->
                contactsRepository.getUserContacts(page, pageSize)
            }
        )
    }
}