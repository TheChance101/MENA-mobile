package net.thechance.mena.core_chat.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bilalazzam.contacts_provider.ContactsProvider
import com.bilalazzam.contacts_provider.ContactsProviderFactory
import kotlinx.cinterop.ExperimentalForeignApi
import net.thechance.mena.core_chat.data.source.local.database.CHAT_DATABASE_NAME
import net.thechance.mena.core_chat.data.source.local.database.ChatDatabase
import net.thechance.mena.core_chat.data.source.local.datastore.createDataStore
import net.thechance.mena.core_chat.data.source.local.datastore.dataStoreName
import org.koin.core.scope.Scope
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun Scope.createContactsProvider(): ContactsProvider {
    return ContactsProviderFactory().createContactsProvider()
}

@OptIn(ExperimentalForeignApi::class)
actual fun Scope.createSettingsDataStore(): DataStore<Preferences> {
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

actual fun Scope.getDatabaseBuilder(): RoomDatabase.Builder<ChatDatabase> {
    val dbFilePath = documentDirectory() + "/" + CHAT_DATABASE_NAME
    return Room.databaseBuilder<ChatDatabase>(name = dbFilePath)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
