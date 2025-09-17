package net.thechance.mena.trends.data.repository

import kotlinx.datetime.LocalDateTime

import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelRepository
import org.koin.core.annotation.Single

@Single(binds = [ReelRepository::class])
class FakeReelsRepositoryImpl : ReelRepository {

    private val categories = listOf(
        Category(id = 1, name = "Sport", emoji = "⚽"),
        Category(id = 2, name = "Food", emoji = "🍔"),
        Category(id = 3, name = "Code", emoji = "💻")
    )

    private val reel = Reel(
        id = 1,
        thumbnailUrl = "https://example.com/thumb1.jpg",
        videoUrl = "https://example.com/video1.mp4",
        description = "First Reel",
        likesCount = 22,
        viewsCount = 150,
        createdAt = LocalDateTime(2002, 2, 22, 5, 15),
        categories = categories
    )

    private val reels: MutableList<Reel> = mutableListOf(
        reel,
        reel.copy(id = 2, description = "Second Reel", likesCount = 10, viewsCount = 40),
        reel.copy(id = 3, description = "Third Reel", likesCount = 100, viewsCount = 415),
        reel.copy(id = 4, description = "Fourth Reel", likesCount = 5, viewsCount = 8),
        reel.copy(id = 5, description = "Fifth Reel", likesCount = 0, viewsCount = 1000)
    )

    override suspend fun deleteReelById(id: Int) {
        reels.removeAll { it.id == id }
    }

    override suspend fun getAllReels(): List<Reel> = reels.toList()
}