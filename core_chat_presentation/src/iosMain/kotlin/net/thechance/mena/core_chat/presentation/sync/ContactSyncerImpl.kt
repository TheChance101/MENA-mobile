package net.thechance.mena.core_chat.presentation.sync

import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.screen.contacts.ContactSyncer
import org.koin.core.component.KoinComponent

class ContactSyncerImpl(
    private val contactsRepository: ContactsRepository
) : ContactSyncer, KoinComponent {

    override suspend fun sync() {
        contactsRepository.syncContacts()
    }
}