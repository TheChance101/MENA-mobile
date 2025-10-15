package net.thechance.mena.faith.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import net.thechance.mena.faith.domain.entity.Ayah

class TilawahDataStore(private val dataStore: DataStore<Preferences>) {
    suspend fun saveLastAyah(surahId: Int, ayahNumber: Int) {
        dataStore.edit { prefs ->
            prefs[AYAH_NUMBER] = ayahNumber
            prefs[SURAH_ID] = surahId
        }
    }

    val lastAyahFlow: Flow<Ayah?> = dataStore.data.map { prefs ->
        val ayahNumber = prefs[AYAH_NUMBER] ?: return@map null
        val surahId = prefs[SURAH_ID] ?: return@map null

        Ayah(
            number = ayahNumber,
            surahId = surahId,
            content = prefs[AYAH_CONTENT] ?: "",
            plainContent = prefs[AYAH_PLAIN_CONTENT] ?: ""
        )
    }

    suspend fun getLastAyah(): Ayah? {
        return lastAyahFlow.first()
    }

    private companion object {
        val AYAH_NUMBER = intPreferencesKey("ayah_number")
        val SURAH_ID = intPreferencesKey("surah_id")
        val AYAH_CONTENT = stringPreferencesKey("ayah_content")
        val AYAH_PLAIN_CONTENT = stringPreferencesKey("ayah_plain_content")

    }
}