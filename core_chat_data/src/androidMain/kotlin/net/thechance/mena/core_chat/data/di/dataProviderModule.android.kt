package net.thechance.mena.core_chat.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bilalazzam.contacts_provider.ContactsProvider
import com.bilalazzam.contacts_provider.ContactsProviderFactory
import net.thechance.mena.core_chat.data.source.local.database.CHAT_DATABASE_NAME
import net.thechance.mena.core_chat.data.source.local.database.ChatDatabase
import net.thechance.mena.core_chat.data.source.local.datastore.createDataStore
import net.thechance.mena.core_chat.data.source.local.datastore.dataStoreName
import org.koin.core.scope.Scope

actual fun Scope.createContactsProvider(): ContactsProvider {
    return ContactsProviderFactory(this.get()).createContactsProvider()
}

actual fun Scope.createSettingsDataStore(): DataStore<Preferences> {
    val context = this.get<Context>()
    return createDataStore {
        context.filesDir.resolve(dataStoreName).absolutePath
    }
}

actual fun Scope.getDatabaseBuilder(): RoomDatabase.Builder<ChatDatabase> {
    val context = this.get<Context>()
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(CHAT_DATABASE_NAME)
    return Room.databaseBuilder<ChatDatabase>(context = appContext, name = dbFile.absolutePath)
}