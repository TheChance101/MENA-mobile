package net.thechance.mena.faith.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import net.thechance.mena.faith.domain.model.LastAyahForTilawah

class TilawahDataStore(private val dataStore: DataStore<Preferences>) : ITilawahDataStore {
    override suspend fun saveLastAyah(ayah: LastAyahForTilawah) {
        dataStore.edit { prefs ->
            prefs[AYAH_NUMBER] = ayah.number
            prefs[SURAH_ID] = ayah.surahId
            prefs[SURAH_NAME] = ayah.surahName
        }
    }

    override val lastAyahFlow: Flow<LastAyahForTilawah?> = dataStore.data.map { prefs ->

        LastAyahForTilawah(
            number = prefs[AYAH_NUMBER] ?: return@map null,
            surahId = prefs[SURAH_ID] ?: return@map null,
            surahName = prefs[SURAH_NAME] ?: return@map null
        )
    }

    override suspend fun getLastAyah(): LastAyahForTilawah? = lastAyahFlow.last()

    private companion object {
        val AYAH_NUMBER = intPreferencesKey("ayah_number")
        val SURAH_ID = intPreferencesKey("surah_id")
        val SURAH_NAME = stringPreferencesKey("surah_name")

    }
}
