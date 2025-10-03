package net.thechance.mena.faith.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.mapper.ayahBookmark.toAyahBookmark
import net.thechance.mena.faith.data.mapper.toAyah
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.data.remote.dto.bookmark.AddBookmarkRequest
import net.thechance.mena.faith.data.remote.service.BookmarkApiService
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImpl(
    private val ayahDao: AyahDao,
    private val bookmarkApiService: BookmarkApiService
) : BookmarkRepository {

    override suspend fun addAyahBookmark(surahId: Int, ayahNumber: Int): AyahBookmark {
        val dto = bookmarkApiService.addBookmark(AddBookmarkRequest(surahId, ayahNumber))
        val surah = ayahDao.getSurah(surahId)
        val ayah = ayahDao.getAyah(surahId, ayahNumber)

        return AyahBookmark(
            id = dto.id.toInt(),
            surah = surah.toSurah(),
            ayah = ayah.toAyah(),
            createdAt = Instant.parse(dto.createdAt)
        )
    }

    override suspend fun getAyahBookmarks(pageNumber: Int, pageSize: Int): List<AyahBookmark> {
        val response = bookmarkApiService.getBookmarks(pageNumber, pageSize)
        return response.items.mapAsync {
            it.toAyahBookmark(
                fetchSurah = ayahDao::getSurah,
                fetchAyah = ayahDao::getAyah
            )
        }
    }

    override suspend fun deleteAyahBookmark(ayahBookmarkId: Int) {
        bookmarkApiService.deleteBookmark(ayahBookmarkId)
    }

    private suspend fun <DTO, ENTITY> Collection<DTO>.mapAsync(transform: suspend (DTO) -> ENTITY): List<ENTITY> {
        return coroutineScope {
            map { async { transform(it) } }.awaitAll()
        }
    }
}

