package net.thechance.mena.faith.presentation.util

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.presentation.feature.quran.search.SearchResult

fun Ayah.toSearchResult(surahName: String? = null) = SearchResult(
    number = number,
    surahId = surahId,
    surahName = surahName ?: Surah.SurahOrder.entries[surahId - 1].name,
    content = content,
    plainContent = plainContent
)