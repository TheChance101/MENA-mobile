package net.thechance.mena.trends.utils

import io.ktor.http.HttpStatusCode
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository


class FakeRepository(): ReelsRepository {
    override suspend fun deleteReelById(id: String) {}

    override suspend fun getAllReels(pageNumber: Int): List<Reel> {
      return mockkReels
    }
}

val mockkReels = listOf(
    Reel(
        id = "1",
        thumbnailUrl = "https://example.com/reel1.jpg",
        videoUrl = "https://example.com/reel1.mp4",
        description = "Funny reel about Kotlin Multiplatform",
        likesCount = 120,
        viewsCount = 1500,
        createdAt = LocalDateTime(year = 2025, month = 9, day = 16, hour = 15, minute = 6, second = 57),
        categories = listOf(
            Category(id = "1", name = "Comedy", emoji = "😂"),
            Category(id = "2", name = "Tech", emoji = "💻")
        )
    ),
    Reel(
        id = "2",
        thumbnailUrl = "https://example.com/reel2.jpg",
        videoUrl = "https://example.com/reel2.mp4",
        description = "Travel vlog reel",
        likesCount = 85,
        viewsCount = 980,
        createdAt = LocalDateTime(year = 2025, month = 9, day = 16, hour = 15, minute = 6, second = 57),
        categories = listOf(
            Category(id = "3", name = "Travel", emoji = "✈️")
        )
    )
)