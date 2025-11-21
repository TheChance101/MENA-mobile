package net.thechance.mena.faith.domain.service

import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository

open class QuranService(private val repository: QuranRepository) {
    open suspend fun getSurahDetails(surahId: Int): Surah = repository.getSurahById(surahId)
}