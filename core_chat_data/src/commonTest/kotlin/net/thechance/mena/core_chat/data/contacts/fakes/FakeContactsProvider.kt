package net.thechance.mena.core_chat.data.contacts.fakes

import com.bilalazzam.contacts_provider.ContactField
import com.bilalazzam.contacts_provider.ContactsPermissionDeniedException
import com.bilalazzam.contacts_provider.ContactsProvider
import com.bilalazzam.contacts_provider.Contact as DeviceContact


class FakeContactsProvider(
    private var contacts: List<DeviceContact> = listOf()
) : ContactsProvider {
    var getAllContactsCalled = false
    var throwPermissionDenied = false

    override suspend fun getAllContacts(fields: Set<ContactField>): List<DeviceContact> {
        getAllContactsCalled = true
        if (throwPermissionDenied) {
            throw ContactsPermissionDeniedException()
        }
        return contacts
    }

    fun setContacts(newContacts: List<DeviceContact>) {
        contacts = newContacts
    }

    fun reset() {
        getAllContactsCalled = false
        throwPermissionDenied = false
    }
}