package net.thechance.mena.faith.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(): DataStore<Preferences>
internal const val dataStoreFileName = "tilawah_datastore.preferences_pb"