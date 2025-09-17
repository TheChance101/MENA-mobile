package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah

interface QuranRepository {
    suspend fun getAllSur(): List<Surah>
    suspend fun getSurahDetails(id: Int): List<Ayah>

}
