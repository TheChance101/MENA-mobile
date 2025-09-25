package net.thechance.mena.core_chat.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bilalazzam.contacts_provider.ContactsProvider
import com.bilalazzam.contacts_provider.ContactsProviderFactory
import net.thechance.mena.core_chat.data.contacts.utils.createDataStore
import net.thechance.mena.core_chat.data.contacts.utils.dataStoreName
import org.koin.core.scope.Scope

actual fun Scope.createContactsProvider(): ContactsProvider {
    return ContactsProviderFactory(this.get()).createContactsProvider()
}

actual fun Scope.createSettingsDataStore(): DataStore<Preferences>{
    val context = this.get<Context>()
    return createDataStore {
        context.filesDir.resolve(dataStoreName).absolutePath
    }
}