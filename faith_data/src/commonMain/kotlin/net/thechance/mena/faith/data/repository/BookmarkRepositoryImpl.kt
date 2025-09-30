package net.thechance.mena.faith.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.mapper.ayahBookmark.toAyah
import net.thechance.mena.faith.data.mapper.ayahBookmark.toAyahBookmark
import net.thechance.mena.faith.data.mapper.toSurah
import net.thechance.mena.faith.data.remote.dto.PageResponse
import net.thechance.mena.faith.data.remote.dto.bookmark.AddBookmarkRequest
import net.thechance.mena.faith.data.remote.dto.bookmark.AyahBookmarkDto
import net.thechance.mena.faith.data.utils.safeApiCall
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.PagedFetchResponse
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImpl(
    private val ayahDao: AyahDao,
    private val httpClient: HttpClient,
) : BookmarkRepository {

    override suspend fun addAyahBookmark(surahId: Int, ayahNumber: Int): AyahBookmark {
        return coroutineScope {
            val bookmarkResponse = async { httpClient.postBookmark(surahId, ayahNumber) }
            val surah = async { ayahDao.getSurah(surahId) }
            val ayah = async { ayahDao.getAyah(surahId, ayahNumber) }

            AyahBookmark(
                id = bookmarkResponse.await().id.toInt(),
                surah = surah.await().toSurah(),
                ayah = ayah.await().toAyah(),
                createdAt = Instant.parse(bookmarkResponse.await().createdAt)
            )
        }
    }

    override suspend fun getAyahBookmarks(pageNumber: Int): PagedFetchResponse<AyahBookmark> {
        return coroutineScope {
            val response = httpClient.getBookmarks(pageNumber)

            PagedFetchResponse(
                currentPage = pageNumber,
                items = response.items.mapAsync {
                    it.toAyahBookmark(
                        fetchSurah = ayahDao::getSurah,
                        fetchAyah = ayahDao::getAyah
                    )
                },
                totalPages = response.totalPages,
                totalItems = response.totalItems
            )
        }
    }

    override suspend fun deleteAyahBookmark(ayahBookmarkId: Int) {
        safeApiCall<Unit> {
            httpClient.delete("$DELETE_AYAH_BOOKMARK_END_POINT/$ayahBookmarkId")
        }
    }

    private suspend fun HttpClient.postBookmark(surahId: Int, ayahNumber: Int): AyahBookmarkDto {
        return safeApiCall {
            post(POST_AYAH_BOOKMARK_END_POINT) {
                setBody(AddBookmarkRequest(surahId, ayahNumber))
            }.body()
        }
    }

    private suspend fun HttpClient.getBookmarks(pageNumber: Int): PageResponse<AyahBookmarkDto> {
        return safeApiCall {
            get(GET_AYAH_BOOKMARK_END_POINT) {
                parameter("page", pageNumber)
            }.body()
        }
    }

    private suspend fun <DTO, ENTITY> Collection<DTO>.mapAsync(transform: suspend (DTO) -> ENTITY): List<ENTITY> {
        return coroutineScope {
            map { async { transform(it) } }.awaitAll()
        }
    }

    private companion object {
        const val POST_AYAH_BOOKMARK_END_POINT = "faith/ayah/bookmark"
        const val GET_AYAH_BOOKMARK_END_POINT = "faith/ayah/bookmark"
        const val DELETE_AYAH_BOOKMARK_END_POINT = "faith/ayah/bookmark"
    }

}
