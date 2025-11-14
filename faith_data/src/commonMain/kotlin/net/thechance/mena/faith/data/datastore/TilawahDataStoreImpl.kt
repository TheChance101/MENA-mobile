package net.thechance.mena.faith.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import net.thechance.mena.faith.domain.model.LastAyahForTilawah

class TilawahDataStoreImpl(private val dataStore: DataStore<Preferences>) : TilawahDataStore {
    override suspend fun saveLastAyah(ayah: LastAyahForTilawah) {
        dataStore.edit { prefs ->
            prefs[AYAH_NUMBER] = ayah.number
            prefs[SURAH_ID] = ayah.surahId
        }
    }

    override val lastAyahFlow: Flow<LastAyahForTilawah?> = dataStore.data.map { prefs ->

        LastAyahForTilawah(
            number = prefs[AYAH_NUMBER] ?: return@map null,
            surahId = prefs[SURAH_ID] ?: return@map null,
        )
    }

    override suspend fun getLastAyah(): LastAyahForTilawah? = lastAyahFlow.first()

    override suspend fun saveDefaultReciter(reciterId: Int) {
        dataStore.edit { prefs ->
            prefs[DEFAULT_RECITER] = reciterId
        }
    }

    override suspend fun getDefaultReciter(): Flow<Int> {
        return dataStore.data.map { prefs ->
            prefs[DEFAULT_RECITER] ?: 1
        }
    }

    private companion object {
        val AYAH_NUMBER = intPreferencesKey("ayah_number")
        val SURAH_ID = intPreferencesKey("surah_id")
        val DEFAULT_RECITER = intPreferencesKey("default_reciter")

    }
}
