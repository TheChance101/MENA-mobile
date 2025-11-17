package net.thechance.mena.faith.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import de.jensklingenberg.ktorfit.Response
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.remote.model.PageResponse
import net.thechance.mena.faith.data.remote.model.bookmark.AddBookmarkRequest
import net.thechance.mena.faith.data.remote.model.bookmark.AyahBookmarkDto
import net.thechance.mena.faith.data.remote.service.BookmarkApiService
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.service.LocalizationService
import net.thechance.mena.identity.domain.util.AppLanguage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImplTest {

    private val mockDao: AyahDao = MockAyahDao()
    private val apiService: BookmarkApiService = mock(MockMode.autofill)
    private val settingsRepository: SettingsRepository = mock(MockMode.autofill)
    private lateinit var repository: BookmarkRepository

    @BeforeTest
    fun setup() {
        repository = BookmarkRepositoryImpl(
            ayahDao = mockDao,
            bookmarkApiService = apiService,
            localizationService = LocalizationService(settingsRepository)
        )
    }

    @Test
    fun `getAyahBookmarks should return a paged response of bookmarks when the user has already bookmarked Ayat`() =
        runTest {
            // Given
            everySuspend {
                apiService.getBookmarks(any(), any())
            } returns successfulGetBookmarkResponse()
            everySuspend { settingsRepository.getCurrentAppLanguage() } returns AppLanguage.ENGLISH

            // When
            val response = repository.getAyahBookmarks(PAGE_NUMBER, PAGE_SIZE)

            // Then
            assertThat(response).isEqualTo(AYAH_BOOKMARK_LIST)
        }

    @Test
    fun `getAyahBookmarks should call API with correct parameters`() = runTest {
        // Given
        everySuspend {
            apiService.getBookmarks(any(), any())
        } returns successfulGetBookmarkResponse()
        everySuspend { settingsRepository.getCurrentAppLanguage() } returns AppLanguage.ENGLISH

        // When
        repository.getAyahBookmarks(PAGE_NUMBER, PAGE_SIZE)

        // Then
        verifySuspend {
            apiService.getBookmarks(PAGE_NUMBER, PAGE_SIZE)
        }
    }

    @Test
    fun `addAyahBookmark should add bookmark successfully to bookmark list when the user add new bookmark`() =
        runTest {
            // Given
            everySuspend { apiService.addBookmark(any()) } returns successfulAddBookmarkResponse()
            everySuspend { settingsRepository.getCurrentAppLanguage() } returns AppLanguage.ENGLISH

            // When
            val bookmark = repository.addAyahBookmark(surahId = 1, ayahNumber = 1)

            // Then
            assertThat(bookmark).isEqualTo(Unit)
        }

    @Test
    fun `addAyahBookmark should call API with correct request`() = runTest {
        // Given
        everySuspend { apiService.addBookmark(any()) } returns successfulAddBookmarkResponse()
        everySuspend { settingsRepository.getCurrentAppLanguage() } returns AppLanguage.ENGLISH

        // When
        repository.addAyahBookmark(surahId = 1, ayahNumber = 1)

        // Then
        verifySuspend {
            apiService.addBookmark(AddBookmarkRequest(1, 1))
        }
    }

    @Test
    fun `deleteAyahBookmark should delete the selected bookmark successfully by id when the user delete bookmark`() =
        runTest {
            // Given
            everySuspend { apiService.deleteBookmark(any()) } returns successfulDeleteResponse()

            // When
            val result = runCatching { repository.deleteAyahBookmark(1) }

            // Then
            assertThat(result).isSuccess()
        }

    @Test
    fun `deleteAyahBookmark should call API with correct bookmark id`() = runTest {
        // Given
        everySuspend { apiService.deleteBookmark(any()) } returns successfulDeleteResponse()

        // When
        repository.deleteAyahBookmark(1)

        // Then
        verifySuspend { apiService.deleteBookmark(1) }
    }

    @OptIn(InternalAPI::class)
    private fun successfulGetBookmarkResponse(): Response<PageResponse<AyahBookmarkDto>> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.OK
        }

        return Response.success(
            body = PageResponse(
                currentPage = 0,
                items = fakeBookmarkList,
                totalPages = 1,
                totalItems = 2
            ),
            rawResponse = mockHttpResponse
        ) as Response<PageResponse<AyahBookmarkDto>>
    }

    @OptIn(InternalAPI::class)
    private fun successfulAddBookmarkResponse(): Response<Unit> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.OK
        }

        return Response.success(
            body = AYAH_BOOKMARK_ITEM_DTO,
            rawResponse = mockHttpResponse
        ) as Response<Unit>
    }

    @OptIn(InternalAPI::class)
    private fun successfulDeleteResponse(): Response<Unit> {
        val mockHttpResponse: HttpResponse = mock(MockMode.autofill) {
            everySuspend { status } returns HttpStatusCode.OK
        }

        return Response.success(
            body = Unit,
            rawResponse = mockHttpResponse
        ) as Response<Unit>
    }

    private companion object {
        const val PAGE_NUMBER = 0
        const val PAGE_SIZE = 10

        private val fakeBookmarkList = listOf(
            AyahBookmarkDto(
                id = "1",
                surahId = 1,
                ayahNumber = 1,
                createdAt = "2023-01-01T00:00:00Z"
            ),
            AyahBookmarkDto(
                id = "2",
                surahId = 2,
                ayahNumber = 5,
                createdAt = "2023-02-01T00:00:00Z"
            )
        )

        val AYAH_BOOKMARK_LIST = listOf(
            AyahBookmark(
                id = 1,
                surah = Surah(
                    id = 1,
                    order = Surah.SurahOrder.AlFatihah,
                    name = "Al-Fatiha",
                    ayahCount = 2,
                ),
                ayah = Ayah(
                    number = 1,
                    surahId = 1,
                    content = "Ayah content",
                    plainContent = "Ayah plain content"
                ),
                createdAt = Instant.parse("2023-01-01T00:00:00Z")
            ),
            AyahBookmark(
                id = 2,
                surah = Surah(
                    id = 2,
                    order = Surah.SurahOrder.AlBaqarah,
                    name = "Al-Baqarah",
                    ayahCount = 1,
                ),
                ayah = Ayah(
                    number = 5,
                    surahId = 2,
                    content = "Ayah content",
                    plainContent = "Ayah plain content"
                ),
                createdAt = Instant.parse("2023-02-01T00:00:00Z")
            )
        )

        val AYAH_BOOKMARK_ITEM_DTO = AyahBookmarkDto(
            id = "1",
            surahId = 1,
            ayahNumber = 1,
            createdAt = "2023-01-01T00:00:00Z"
        )

    }
}