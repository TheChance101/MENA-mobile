package net.thechance.mena.core_chat.presentation.sync

import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactSyncer

class ContactSyncerImpl(
    private val contactsRepository: ContactsRepository
) : ContactSyncer {

    override suspend fun sync() {
        //TODO: implement iOS contact sync
        contactsRepository.syncContacts()
    }
}