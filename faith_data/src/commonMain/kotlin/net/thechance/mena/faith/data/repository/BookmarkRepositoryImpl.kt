package net.thechance.mena.faith.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.mapper.ayahBookmark.toAyahBookmark
import net.thechance.mena.faith.data.remote.model.PageResponse
import net.thechance.mena.faith.data.remote.model.bookmark.AddBookmarkRequest
import net.thechance.mena.faith.data.remote.model.bookmark.AyahBookmarkDto
import net.thechance.mena.faith.data.remote.service.BookmarkApiService
import net.thechance.mena.faith.data.utils.executeApiSafely
import net.thechance.mena.faith.data.utils.executeLocalSafely
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.identity.domain.service.LocalizationService
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImpl(
    private val ayahDao: AyahDao,
    private val bookmarkApiService: BookmarkApiService,
    private val localizationService: LocalizationService,
) : BookmarkRepository {

    override suspend fun addAyahBookmark(surahId: Int, ayahNumber: Int) {
        executeApiSafely<Unit> {
            bookmarkApiService.addBookmark(
                AddBookmarkRequest(
                    surahId = surahId,
                    ayahNumber = ayahNumber
                )
            )
        }
    }

    override suspend fun getAyahBookmarks(pageNumber: Int, pageSize: Int): List<AyahBookmark> {
        val response = executeApiSafely<PageResponse<AyahBookmarkDto>> {
            bookmarkApiService.getBookmarks(pageNumber = pageNumber, pageSize = pageSize)
        }
        return response.items?.mapAsync {
            it.toAyahBookmark(
                fetchSurah = { surahId ->
                    executeLocalSafely {
                        ayahDao.getSurah(surahId)
                    }
                },
                fetchAyah = { ayahId, surahId ->
                    executeLocalSafely {
                        ayahDao.getAyah(ayahId = ayahId, surahId = surahId)
                    }
                },
                localizationService.getCurrentLanguage()
            )
        } ?: emptyList()
    }

    override suspend fun deleteAyahBookmark(ayahBookmarkId: Int) {
        executeApiSafely<Unit> {
            bookmarkApiService.deleteBookmark(ayahBookmarkId)
        }
    }

    private suspend fun <DTO, ENTITY> Collection<DTO>.mapAsync(transform: suspend (DTO) -> ENTITY): List<ENTITY> {
        return coroutineScope {
            map { async { transform(it) } }.awaitAll()
        }
    }
}