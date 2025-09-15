package net.thechance.mena.core_chat.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bilalazzam.contacts_provider.ContactsProvider
import com.bilalazzam.contacts_provider.ContactsProviderFactory
import kotlinx.cinterop.ExperimentalForeignApi
import net.thechance.mena.core_chat.data.contacts.utils.createDataStore
import net.thechance.mena.core_chat.data.contacts.utils.dataStoreName
import org.koin.core.scope.Scope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun Scope.createContactsProvider(): ContactsProvider {
    return ContactsProviderFactory().createContactsProvider()
}

@OptIn(ExperimentalForeignApi::class)
actual fun Scope.createSettingsDataStore(): DataStore<Preferences>{
    return createDataStore {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$dataStoreName"
    }
}

