package net.thechance.mena.core_chat.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bilalazzam.contacts_provider.ContactsProvider
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal val dataProviderModule = module {
    single { createContactsProvider() }
    single { createSettingsDataStore() }
}

expect fun Scope.createContactsProvider(): ContactsProvider
expect fun Scope.createSettingsDataStore(): DataStore<Preferences>