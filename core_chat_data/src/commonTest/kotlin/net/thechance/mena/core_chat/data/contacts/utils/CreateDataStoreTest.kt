package net.thechance.mena.core_chat.data.contacts.utils
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.thechance.mena.core_chat.data.source.local.datastore.createDataStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class DataStoreFactoryTest {

    @Test
    fun `createDataStore should create usable DataStore when valid path provided`() = runBlocking {
        val dataStore: DataStore<Preferences> = createDataStore { DATA_STORE_PATH }

        assertNotNull(dataStore)

        val testKey = intPreferencesKey(DATA_STORE_TEST_KEY)
        dataStore.edit { prefs -> prefs[testKey] = 99 }
        val value = dataStore.data.first()[testKey]

        assertEquals(99, value)
    }

    companion object {
        private const val DATA_STORE_PATH = "test_datastore.preferences_pb"
        private const val DATA_STORE_TEST_KEY = "test_key"
    }
}
