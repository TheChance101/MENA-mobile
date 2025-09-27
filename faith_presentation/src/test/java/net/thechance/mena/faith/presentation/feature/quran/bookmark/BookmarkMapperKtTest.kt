package net.thechance.mena.faith.presentation.feature.quran.bookmark

import junit.framework.TestCase.assertEquals
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
class BookmarkMapperKtTest {

    @Test
    fun `toUiState should map all fields correctly when given valid AyahBookmark`() {
        val now = Clock.System.now()
        val bookmark = createBookmark(42, sampleSurah, sampleAyah, now)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return empty surahName when surah name is empty`() {
        val now = Clock.System.now()
        val bookmark = createBookmark(surah = sampleSurah.copy(name = ""), createdAt = now)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return zero ayaNumber when ayah number is zero`() {
        val now = Clock.System.now()
        val bookmark = createBookmark(ayah = sampleAyah.copy(number = 0), createdAt = now)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return empty ayaText when ayah content is empty`() {
        val now = Clock.System.now()
        val bookmark = createBookmark(ayah = sampleAyah.copy(content = ""), createdAt = now)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return same ayaText when ayah content has special characters`() {
        val now = Clock.System.now()
        val specialText = "بِسْمِ اللَّهِ\nSpecialChars!@#✨"
        val bookmark =
            createBookmark(ayah = sampleAyah.copy(content = specialText), createdAt = now)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return future timestamp when createdAt is in future`() {
        val future = Instant.DISTANT_FUTURE
        val bookmark = createBookmark(createdAt = future)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return epoch timestamp when createdAt is epoch`() {
        val epoch = Instant.fromEpochMilliseconds(0)
        val bookmark = createBookmark(createdAt = epoch)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return surahName with special characters when surah name contains them`() {
        val now = Clock.System.now()
        val weirdName = "Surah-123!#?"
        val bookmark = createBookmark(surah = sampleSurah.copy(name = weirdName), createdAt = now)

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    @Test
    fun `toUiState should return long strings when surah name and ayah content are very long`() {
        val now = Clock.System.now()
        val longName = "a".repeat(10_000)
        val longContent = "b".repeat(20_000)
        val bookmark = createBookmark(
            surah = sampleSurah.copy(name = longName),
            ayah = sampleAyah.copy(content = longContent),
            createdAt = now
        )

        assertEquals(expectedUiState(bookmark), bookmark.toUiState())
    }

    companion object TestData {
        val sampleSurah = Surah(
            id = 1,
            order = Surah.SurahOrder.AlFath,
            name = "Al-Fatiha",
            ayahCount = 7,
            isMakkia = true
        )

        val sampleAyah = Ayah(
            number = 1,
            content = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
            surahId = 1
        )

        fun createBookmark(
            id: Int = 1,
            surah: Surah = sampleSurah,
            ayah: Ayah = sampleAyah,
            createdAt: Instant = Clock.System.now(),
        ) = AyahBookmark(id, surah, ayah, createdAt)


        fun expectedUiState(
            bookmark: AyahBookmark,
        ) = BookmarksScreenState.BookmarkCardUiState(
            bookmarkId = bookmark.id,
            surahName = bookmark.surah.name,
            ayaNumber = bookmark.ayah.number,
            ayaText = bookmark.ayah.content,
            createdAt = bookmark.createdAt
        )
    }
}
