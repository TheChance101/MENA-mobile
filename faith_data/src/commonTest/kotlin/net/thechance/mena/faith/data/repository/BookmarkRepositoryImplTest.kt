package net.thechance.mena.faith.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.remote.dto.PageResponse
import net.thechance.mena.faith.data.remote.dto.bookmark.AyahBookmarkDto
import net.thechance.mena.faith.data.remote.service.BookmarkApiService
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImplTest {

    private val mockDao: AyahDao = MockAyahDao()
    private val apiService: BookmarkApiService = mock(MockMode.autofill)
    private lateinit var repository: BookmarkRepository

    @BeforeTest
    fun setup() {
        repository = BookmarkRepositoryImpl(
            ayahDao = mockDao,
            bookmarkApiService = apiService
        )
    }

    @Test
    fun `getAyahBookmarks should return a paged response of bookmarks when the user has already bookmarked Ayat`() =
        runTest {
            everySuspend { apiService.getBookmarks(any(), any()) } returns fakeBookmarkPageResponse

            // When
            val response = repository.getAyahBookmarks(PAGE_NUMBER, PAGE_SIZE)

            // Then
            assertThat(response).isEqualTo(AYAH_BOOKMARK_LIST)
        }

    @Test
    fun `addAyahBookmark should add bookmark successfully to bookmark list when the user add new bookmark`() =
        runTest {
            // Given
            everySuspend { apiService.addBookmark(any()) } returns AYAH_BOOKMARK_ITEM_DTO

            // When
            val bookmark = repository.addAyahBookmark(surahId = 1, ayahNumber = 1)

            // Then
            assertThat(bookmark).isEqualTo(AYAH_BOOKMARK_ITEM)
        }

    @Test
    fun `deleteAyahBookmark should delete the selected bookmark successfully by id when the user delete bookmark`() =
        runTest {
            // Given
            everySuspend { apiService.deleteBookmark(any()) } returns Unit

            // When
            val result = runCatching { repository.deleteAyahBookmark(1) }

            // Then
            assertThat(result).isSuccess()
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

        val fakeBookmarkPageResponse = PageResponse(
            currentPage = 0,
            items = fakeBookmarkList,
            totalPages = 1,
            totalItems = 2
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
                    content = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيمِ",
                    plainContent = "بسم الله الرحمن الرحيم"
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
                    content = "أُوْلَٰٓئِكَ عَلَىٰ هُدٗى مِّن رَّبِّهِمۡۖ وَأُوْلَٰٓئِكَ هُمُ ٱلۡمُفۡلِحُونَ",
                    plainContent = "أولئك على هدى من ربهم وأولئك هم المفلحون"
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

        val AYAH_BOOKMARK_ITEM = AyahBookmark(
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
                content = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيمِ",
                plainContent = "بسم الله الرحمن الرحيم"
            ),
            createdAt = Instant.parse("2023-01-01T00:00:00Z")
        )
    }
}
