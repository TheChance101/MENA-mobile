package net.thechance.mena.faith.data.repository.bookmark

import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Bookmark
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class BookmarkRepositoryImpl: BookmarkRepository {

    override suspend fun getAllBookmarks(): List<Bookmark> = bookmarks

    override suspend fun addBookmark(
        surahId: Int,
        ayahNumber: Int
    ): Bookmark {
        TODO("Not yet implemented")
    }

    override suspend fun removeBookmark(bookmarkId: Int) {
        bookmarks.removeAt(bookmarks.indexOfFirst { it.id == bookmarkId })
    }

    private val bookmarks = List(15) { id ->
        Bookmark(
            id = id,
            surah = Surah(
                id = 1,
                order = Surah.SurahOrder.ALFATIHA,
                name = "Al-Fatiha",
                ayahCount = 7
            ),
            ayah = Ayah(
                number = 1,
                surahId = 1,
                content = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ"
            ),
            createdAt = Clock.System.now()
        )
    }.toMutableList()
}
