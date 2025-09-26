package net.thechance.mena.core_chat.data.contacts.fakes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FakeDataStore(
    initial: Preferences = emptyPreferences()
) : DataStore<Preferences> {

    private val state = MutableStateFlow(initial)

    var throwOnRead = false
    var throwOnUpdate = false

    override val data: Flow<Preferences> = flow {
        if (throwOnRead) {
            throw Exception("read error")
        }
        emit(state.value)
    }

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        if (throwOnUpdate) {
            throw Exception("update error")
        }
        val newValue = transform(state.value)
        state.value = newValue
        return newValue
    }

    suspend fun getSyncStatus(): Boolean {
        return data.first()[USER_SYNCED_STATE_KEY] ?: false
    }

    suspend fun setSyncStatus(state: Boolean) {
        updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[USER_SYNCED_STATE_KEY] = state
            }
        }
    }

    fun reset() {
        state.value = emptyPreferences()
        throwOnRead = false
        throwOnUpdate = false
    }

    private companion object {
        val USER_SYNCED_STATE_KEY = booleanPreferencesKey("user_synced_state_key")

    }
}