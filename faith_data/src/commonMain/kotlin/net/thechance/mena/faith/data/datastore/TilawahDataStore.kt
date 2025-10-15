package net.thechance.mena.faith.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import net.thechance.mena.faith.domain.model.SavedAyah

class TilawahDataStore(private val dataStore: DataStore<Preferences>) {
    suspend fun saveLastAyah(savedAyah: SavedAyah) {
        dataStore.edit { prefs ->
            prefs[AYAH_NUMBER] = savedAyah.number
            prefs[SURAH_ID] = savedAyah.surahId
        }
    }

    val lastAyahFlow: Flow<SavedAyah?> = dataStore.data.map { prefs ->
        val ayahNumber = prefs[AYAH_NUMBER] ?: return@map null
        val surahId = prefs[SURAH_ID] ?: return@map null

        SavedAyah(
            number = ayahNumber,
            surahId = surahId,
        )
    }

    suspend fun getLastAyah(): SavedAyah? {
        return lastAyahFlow.first()
    }

    private companion object {
        val AYAH_NUMBER = intPreferencesKey("ayah_number")
        val SURAH_ID = intPreferencesKey("surah_id")

    }
}
