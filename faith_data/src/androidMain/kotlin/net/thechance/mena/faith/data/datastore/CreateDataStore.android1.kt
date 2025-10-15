package net.thechance.mena.faith.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.java.KoinJavaComponent.getKoin

actual fun createDataStore(): DataStore<Preferences> {
    val context: Context = getKoin().get()
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.filesDir
                .resolve(dataStoreFileName)
                .absolutePath
                .toPath()
        }
    )
}