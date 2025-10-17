package net.thechance.mena.trends.presentation.screen.show_real

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.presentation.shared.util.timeAgoValue
import kotlin.test.Test

class ReelExtensionTest {

    @Test
    fun `toUiState should convert Reel to TrendUiState correctly`() {
        val result = testReel1.toUiState()

        assertThat(result).isEqualTo(testTrendUiState1)
    }

    @Test
    fun `toUiState should handle null createdAt correctly`() {
        val result = testReel2.toUiState()

        assertThat(result).isEqualTo(testTrendUiState2)
    }

    @Test
    fun `toUiState should handle categories correctly`() {
        val result = testReel3.toUiState()

        assertThat(result).isEqualTo(testTrendUiState3)
    }

    @Test
    fun `toUiState should handle empty strings correctly`() {
        val result = testReel4.toUiState()

        assertThat(result).isEqualTo(testTrendUiState4)
    }

    private companion object {
        private val testCreatedAt = LocalDateTime.parse("2023-10-15T14:30:00")

        private val testCategories = listOf(
            Category(id = "1", name = "Sports", emoji = "🏀", isSelected = false),
            Category(id = "2", name = "Technology", emoji = "💻", isSelected = false)
        )

        // Test Reels
        val testReel1 = Reel(
            id = "1",
            thumbnailUrl = "thumb1.jpg",
            videoUrl = "video1.mp4",
            description = "Test description",
            likesCount = 10,
            viewsCount = 100,
            createdAt = testCreatedAt,
            userName = "Alice",
            profileImageUrl = "https://example.com/alice.jpg",
            isCurrentUserOwner = false,
            isLiked = false
        )

        val testReel2 = Reel(
            id = "2",
            thumbnailUrl = "thumb2.jpg",
            videoUrl = "video2.mp4",
            description = "Another description",
            likesCount = 20,
            viewsCount = 200,
            createdAt = null,
            userName = "Bob",
            profileImageUrl = "https://example.com/bob.jpg",
            isCurrentUserOwner = true,
            isLiked = false
        )

        val testReel3 = Reel(
            id = "3",
            thumbnailUrl = "thumb3.jpg",
            videoUrl = "video3.mp4",
            description = "Description with categories",
            likesCount = 30,
            viewsCount = 300,
            createdAt = testCreatedAt,
            userName = "Charlie",
            profileImageUrl = "https://example.com/charlie.jpg",
            isCurrentUserOwner = false,
            isLiked = false
        )

        val testReel4 = Reel(
            id = "4",
            thumbnailUrl = "",
            videoUrl = "",
            description = "",
            likesCount = 0,
            viewsCount = 0,
            createdAt = null,
            userName = "",
            profileImageUrl = "",
            isCurrentUserOwner = false,
            isLiked = false
        )


        // Expected ReelUiStates
        val testTrendUiState1 = ReelUiState(
            id = "1",
            profileImageUrl = "https://example.com/alice.jpg",
            userName = "Alice",
            timeAgo = testCreatedAt.timeAgoValue(),
            thumbnailUrl = "thumb1.jpg",
            videoUrl = "video1.mp4",
            description = "Test description",
            likesCount = 10,
            viewsCount = 100
        )

        val testTrendUiState2 = ReelUiState(
            id = "2",
            profileImageUrl = "https://example.com/bob.jpg",
            userName = "Bob",
            timeAgo = null,
            thumbnailUrl = "thumb2.jpg",
            videoUrl = "video2.mp4",
            description = "Another description",
            likesCount = 20,
            viewsCount = 200
        )

        val testTrendUiState3 = ReelUiState(
            id = "3",
            profileImageUrl = "https://example.com/charlie.jpg",
            userName = "Charlie",
            timeAgo = testCreatedAt.timeAgoValue(),
            thumbnailUrl = "thumb3.jpg",
            videoUrl = "video3.mp4",
            description = "Description with categories",
            likesCount = 30,
            viewsCount = 300
        )

        val testTrendUiState4 = ReelUiState(
            id = "4",
            profileImageUrl = "",
            userName = "",
            timeAgo = null,
            thumbnailUrl = "",
            videoUrl = "",
            description = "",
            likesCount = 0,
            viewsCount = 0
        )
    }
}