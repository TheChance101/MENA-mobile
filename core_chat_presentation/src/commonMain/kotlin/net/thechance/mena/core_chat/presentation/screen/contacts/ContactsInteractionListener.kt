package net.thechance.mena.core_chat.presentation.screen.contacts

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ContactsScreenInteractionListener : ContactListInteractionListener{
    fun onBackClicked()
    fun onReSyncClicked()
}

@OptIn(ExperimentalUuidApi::class)
interface ContactListInteractionListener{
    fun onContactClicked(contactId: Uuid?)
    fun onRefreshContactsClicked()
}