package net.thechance.mena.faith.presentation.feature.quran.bookmark

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.AyahBookmark
import net.thechance.mena.faith.domain.entity.Surah
import kotlin.test.Test
import kotlin.test.assertEquals
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
        )

        val sampleAyah = Ayah(
            number = 1,
            content = "بسم الله الرحمن الرحيم",
            surahId = 1,
            plainContent = "بسم الله الرحمن الرحيم"
        )

        fun createBookmark(
            id: Int = 1,
            surah: Surah = sampleSurah,
            ayah: Ayah = sampleAyah,
            createdAt: Instant = Clock.System.now(),
        ) = AyahBookmark(id, surah, ayah, createdAt)


        fun expectedUiState(
            bookmark: AyahBookmark,
        ) = BookMarkUiState.BookmarkCardUiState(
            bookmarkId = bookmark.id,
            surahName = bookmark.surah.name,
            ayaNumber = bookmark.ayah.number,
            ayaText = bookmark.ayah.content,
            createdAt = TimeAgo(
                amount = bookmark.createdAt.let { createdAt ->
                    val now = Clock.System.now()
                    when {
                        createdAt > now -> 0
                        else -> (now - createdAt).inWholeSeconds.toInt()
                    }
                },
                unit = TimeUnit.SECONDS
            )
        )
    }
}