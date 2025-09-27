package net.thechance.mena.core_chat.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.bilalazzam.contacts_provider.ContactsProvider
import net.thechance.mena.core_chat.data.database.ChatDatabase
import net.thechance.mena.core_chat.data.database.dao.MessageDao
import net.thechance.mena.core_chat.data.database.getChatDatabase
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal val dataProviderModule = module {
    single { createContactsProvider() }
    single { createSettingsDataStore() }
    single { getDatabaseBuilder() }

    single<ChatDatabase> { getChatDatabase(get()) }
    single<MessageDao> { get<ChatDatabase>().getMessageDao() }
}

expect fun Scope.createContactsProvider(): ContactsProvider
expect fun Scope.createSettingsDataStore(): DataStore<Preferences>
expect fun Scope.getDatabaseBuilder(): RoomDatabase.Builder<ChatDatabase>