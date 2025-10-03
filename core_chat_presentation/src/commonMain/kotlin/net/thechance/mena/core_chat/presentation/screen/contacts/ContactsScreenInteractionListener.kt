package net.thechance.mena.core_chat.presentation.screen.contacts

interface ContactsScreenInteractionListener : ContactListInteractionListener{
    fun onBackClick()
    fun onReSyncClick()
}

interface ContactListInteractionListener{
    fun onContactClick(contactId: String?)
    fun onRefreshContacts()
}