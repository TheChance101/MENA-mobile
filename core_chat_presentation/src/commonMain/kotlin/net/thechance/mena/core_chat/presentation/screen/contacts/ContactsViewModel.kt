package net.thechance.mena.core_chat.presentation.screen.contacts

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.could_not_load_the_contacts
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.exception.ChatException
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatDetailsRoute
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.navigation.NavigationConstants.IS_SYNC_SUCCESS
import net.thechance.mena.core_chat.presentation.navigation.SyncContactsRoute
import net.thechance.mena.core_chat.presentation.shared.BasePagingSource
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.UiText

class ContactsViewModel(
    private val contactsRepository: ContactsRepository,
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
                    onRefreshContacts()
                    setNavigationArgs(IS_SYNC_SUCCESS to false)
                }
            }
        )
    }

    private fun loadContacts() {
        tryToCollect(
            collect = ::loadContactsOperation,
            onCollect = ::onLoadContactsSuccess,
            onError = ::onDataLoadError
        )
    }

    private fun loadContactsOperation(): Flow<PagingData<ContactUiState>> {
        return createPagingFlow(
            pagingSourceFactory = { createContactsPagingSource() },
            mapper = Contact::toUiModel
        )
    }

    private fun onLoadContactsSuccess(pagingData: PagingData<ContactUiState>?) {
        updateState { it.copy(contacts = flowOf(pagingData ?: PagingData.empty())) }
    }

    override fun onRefreshContacts() {
        loadContacts()
    }

    override fun onBackClick() {
        popBackStack()
    }

    override fun onReSyncClick() {
        navigate(SyncContactsRoute(forceSync = true))
    }

    override fun onContactClick(contactId: Int) {
        navigate(ChatDetailsRoute(contactId = contactId))
    }

    private fun onDataLoadError(e: Throwable) {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.something_went_wrong),
                message = UiText.StringRes(Res.string.could_not_load_the_contacts),
            )
        )
    }

    private fun createContactsPagingSource(onError: ((ChatException) -> Unit)? = ::onDataLoadError)
            : PagingSource<Int, Contact> {
        return BasePagingSource(
            onError = onError,
            fetchItems = { page ->
                contactsRepository.getUserContacts(page)
            }
        )
    }
}