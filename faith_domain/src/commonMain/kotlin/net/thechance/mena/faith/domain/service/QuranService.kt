package net.thechance.mena.faith.domain.service

import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository

class QuranService(private val repository: QuranRepository) {
    suspend fun getSurahDetails(surahId: Int): Surah = repository.getSurahById(surahId)
}