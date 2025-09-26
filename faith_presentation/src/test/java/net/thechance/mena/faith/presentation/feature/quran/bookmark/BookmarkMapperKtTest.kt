package net.thechance.mena.faith.presentation.feature.quran.bookmark

import junit.framework.TestCase.assertEquals
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class BookmarkMapperKtTest {

    private val sampleSurah = Surah(
        id = 1,
        order = Surah.SurahOrder.AlFath,
        name = "Al-Fatiha",
        ayahCount = 7,
        isMakkia = true
    )

    private val sampleAyah = Ayah(
        number = 1,
        content = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
        surahId = 1
    )

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should map all fields correctly when given valid AyahBookmark`() {
        // Given
        val now = Clock.System.now()
        val bookmark = AyahBookmark(42, sampleSurah, sampleAyah, now)

        // When
        val result = bookmark.toUiState()

        // Then
        assertEquals(bookmark.id, result.bookmarkId)
        assertEquals(bookmark.surah.name, result.surahName)
        assertEquals(bookmark.ayah.number, result.ayaNumber)
        assertEquals(bookmark.ayah.content, result.ayaText)
        assertEquals(bookmark.createdAt, result.createdAt)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return empty surahName when surah name is empty`() {
        val bookmark = AyahBookmark(1, sampleSurah.copy(name = ""), sampleAyah, Clock.System.now())
        val result = bookmark.toUiState()
        assertEquals("", result.surahName)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return zero ayaNumber when ayah number is zero`() {
        val bookmark = AyahBookmark(1, sampleSurah, sampleAyah.copy(number = 0), Clock.System.now())
        val result = bookmark.toUiState()
        assertEquals(0, result.ayaNumber)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return empty ayaText when ayah content is empty`() {
        val bookmark =
            AyahBookmark(1, sampleSurah, sampleAyah.copy(content = ""), Clock.System.now())
        val result = bookmark.toUiState()
        assertEquals("", result.ayaText)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return same ayaText when ayah content has special characters`() {
        val specialText = "بِسْمِ اللَّهِ\nSpecialChars!@#✨"
        val bookmark =
            AyahBookmark(1, sampleSurah, sampleAyah.copy(content = specialText), Clock.System.now())
        val result = bookmark.toUiState()
        assertEquals(specialText, result.ayaText)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return future timestamp when createdAt is in future`() {
        val future = Instant.DISTANT_FUTURE
        val bookmark = AyahBookmark(1, sampleSurah, sampleAyah, future)
        val result = bookmark.toUiState()
        assertEquals(future, result.createdAt)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return epoch timestamp when createdAt is epoch`() {
        val epoch = Instant.fromEpochMilliseconds(0)
        val bookmark = AyahBookmark(1, sampleSurah, sampleAyah, epoch)
        val result = bookmark.toUiState()
        assertEquals(epoch, result.createdAt)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return surahName with special characters when surah name contains them`() {
        val weirdName = "Surah-123!#?"
        val bookmark =
            AyahBookmark(1, sampleSurah.copy(name = weirdName), sampleAyah, Clock.System.now())
        val result = bookmark.toUiState()
        assertEquals(weirdName, result.surahName)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `toUiState should return long strings when surah name and ayah content are very long`() {
        val longName = "a".repeat(10_000)
        val longContent = "b".repeat(20_000)
        val bookmark = AyahBookmark(
            1,
            sampleSurah.copy(name = longName),
            sampleAyah.copy(content = longContent),
            Clock.System.now()
        )
        val result = bookmark.toUiState()
        assertEquals(longName, result.surahName)
        assertEquals(longContent, result.ayaText)
    }
}

