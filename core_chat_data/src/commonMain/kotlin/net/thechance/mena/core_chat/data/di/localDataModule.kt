package net.thechance.mena.core_chat.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.bilalazzam.contacts_provider.ContactsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.core_chat.data.source.local.database.ChatDatabase
import net.thechance.mena.core_chat.data.source.local.database.MessageDao
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal val localDataModule = module {
    single { createContactsProvider() }
    single { createSettingsDataStore() }
    single(named(ChatDatabaseBuilder)) { getDatabaseBuilder() }

    single<ChatDatabase> { getChatDatabase(get(named(ChatDatabaseBuilder))) }
    single<MessageDao> { get<ChatDatabase>().getMessageDao() }
}

expect fun Scope.createContactsProvider(): ContactsProvider
expect fun Scope.createSettingsDataStore(): DataStore<Preferences>
expect fun Scope.getDatabaseBuilder(): RoomDatabase.Builder<ChatDatabase>

private fun getChatDatabase(builder: RoomDatabase.Builder<ChatDatabase>): ChatDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

private const val ChatDatabaseBuilder = "ChatDatabaseBuilder"