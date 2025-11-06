package net.thechance.mena.faith.data.datastore

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.faith.domain.model.LastAyahForTilawah

interface TilawahDataStore {
    suspend fun saveLastAyah(ayah: LastAyahForTilawah)
    val lastAyahFlow: Flow<LastAyahForTilawah?>
    suspend fun getLastAyah(): LastAyahForTilawah?

    suspend fun saveDefaultReciter(reciterId: Int)
    suspend fun getDefaultReciter(): Flow<Int>
}