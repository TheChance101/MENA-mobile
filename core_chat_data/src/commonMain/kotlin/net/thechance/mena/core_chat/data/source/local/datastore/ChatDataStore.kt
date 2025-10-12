package net.thechance.mena.core_chat.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(path: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath (produceFile = { path().toPath() })
}

internal const val dataStoreName = "setting.preferences_pb"