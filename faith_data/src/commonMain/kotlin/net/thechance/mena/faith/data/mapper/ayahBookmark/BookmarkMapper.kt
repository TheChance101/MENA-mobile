package net.thechance.mena.faith.data.mapper.ayahBookmark

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.thechance.mena.faith.data.database.AyahDto
import net.thechance.mena.faith.data.database.SurahDto
import net.thechance.mena.faith.data.mapper.toAyah
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.data.remote.model.bookmark.AyahBookmarkDto
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.identity.domain.util.AppLanguage
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
suspend fun AyahBookmarkDto.toAyahBookmark(
    fetchSurah: suspend (Int) -> SurahDto,
    fetchAyah: suspend (ayahNumber: Int, surahId: Int) -> AyahDto,
    appLanguage: AppLanguage
): AyahBookmark {
    return coroutineScope {
        val surahDeferred = async { fetchSurah(surahId) }
        val ayahDeferred = async { fetchAyah(ayahNumber, surahId) }

        val surahEntity = surahDeferred.await()
        val ayahEntity = ayahDeferred.await()

        AyahBookmark(
            id = id.toInt(),
            surah = surahEntity.toSurah(appLanguage),
            ayah = ayahEntity.toAyah(),
            createdAt = Instant.parse(createdAt)
        )
    }
}