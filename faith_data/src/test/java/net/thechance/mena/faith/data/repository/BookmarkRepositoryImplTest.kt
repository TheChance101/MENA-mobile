package net.thechance.mena.faith.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import org.junit.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImplTest {

    private var repository = bookmarkRepository()

    @Test
    fun `getAyahBookmarks should return a paged response of bookmarks when the user has already bookmarked Ayat`() =
        runTest {
            // Given
            val pageNumber = 1
            // When
            val response = repository.getAyahBookmarks(pageNumber)
            // Then
            assertThat(response.currentPage).isEqualTo(pageNumber)
            assertThat(response.items).isEqualTo(AYAH_BOOKMARK_LIST)
        }

    @Test
    fun `addAyahBookmark should add bookmark successfully to bookmark list when the user add new bookmark`() =
        runTest {
            // Given
            val bookmark = repository.addAyahBookmark(surahId = 1, ayahNumber = 1)
            // When & Then
            assertThat(bookmark).isEqualTo(AYAH_BOOKMARK_ITEM)
        }

    @Test
    fun `deleteAyahBookmark should delete the selected bookmark successfully by id when the user delete bookmark`() =
        runTest {
            // Given
            repository = bookmarkRepository(deleteBookmark = { deleteBookmarkResponse() })
            // When
            val result = runCatching { repository.deleteAyahBookmark(1) }
            // Then
            assertThat(result).isSuccess()
        }

    private companion object {
        val AYAH_BOOKMARK_LIST = listOf(
            AyahBookmark(
                id = 1,
                surah = Surah(
                    id = 1,
                    order = Surah.SurahOrder.AlFatihah,
                    name = "Al-Fatiha",
                    ayahCount = 1,
                    isMakkia = true
                ),
                ayah = Ayah(
                    number = 1,
                    surahId = 1,
                    content = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيمِ"
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
                    isMakkia = false
                ),
                ayah = Ayah(
                    number = 5,
                    surahId = 2,
                    content = "أُوْلَٰٓئِكَ عَلَىٰ هُدٗى مِّن رَّبِّهِمۡۖ وَأُوْلَٰٓئِكَ هُمُ ٱلۡمُفۡلِحُونَ"
                ),
                createdAt = Instant.parse("2023-02-01T00:00:00Z")
            )
        )

        val AYAH_BOOKMARK_ITEM = AyahBookmark(
            id = 1,
            surah = Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = "Al-Fatiha",
                ayahCount = 1,
                isMakkia = true
            ),
            ayah = Ayah(
                number = 1,
                surahId = 1,
                content = "بِسْمِ اللهِ الرَّحْمنِ الرَّحِيمِ"
            ),
            createdAt = Instant.parse("2023-01-01T00:00:00Z")
        )
    }
}
