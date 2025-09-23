package net.thechance.mena.faith.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.thechance.mena.faith.data.database.QuranDao
import net.thechance.mena.faith.data.mapper.ayahBookmark.toAyah
import net.thechance.mena.faith.data.mapper.ayahBookmark.toSurah
import net.thechance.mena.faith.data.remote.dto.bookmark.AddBookmarkRequest
import net.thechance.mena.faith.data.remote.dto.bookmark.AyahBookmarkDto
import net.thechance.mena.faith.data.remote.dto.bookmark.AyahBookmarkResponse
import net.thechance.mena.faith.data.utils.safeApiCall
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImpl(
    private val quranDao: QuranDao,
    private val httpClient: HttpClient,
) : BookmarkRepository {

    override suspend fun addAyahBookmark(surahId: Int, ayahNumber: Int): AyahBookmark {
        return coroutineScope {
            val bookmarkResponse = async { httpClient.postBookmark(surahId, ayahNumber) }
            val surah = async { quranDao.getSurah(surahId) }
            val ayah = async { quranDao.getAyah(surahId, ayahNumber) }

            AyahBookmark(
                id = bookmarkResponse.await().id.toInt(),
                surah = surah.await().toSurah(),
                ayah = ayah.await().toAyah(),
                createdAt = Instant.parse(bookmarkResponse.await().createdAt)
            )
        }
    }

    override suspend fun getAllAyahBookmarks(): List<AyahBookmark> {
        return coroutineScope {
            val response = httpClient.getBookmarks()
            response.ayahBookmarks.mapAsync { bookmarkDto ->
                val surah = async { quranDao.getSurah(bookmarkDto.surahId) }
                val ayah = async { quranDao.getAyah(bookmarkDto.surahId, bookmarkDto.ayahNumber) }

                AyahBookmark(
                    id = bookmarkDto.id.toInt(),
                    surah = surah.await().toSurah(),
                    ayah = ayah.await().toAyah(),
                    createdAt = Instant.parse(bookmarkDto.createdAt)
                )
            }
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

    private suspend fun HttpClient.getBookmarks(): AyahBookmarkResponse {
        return safeApiCall {
            get(GET_AYAH_BOOKMARK_END_POINT).body()
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
