package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.data.remote.dto.CategoryDto
import net.thechance.mena.trends.data.remote.dto.TrendDto
import net.thechance.mena.trends.data.remote.mapper.toEntity
import kotlin.test.Test

internal class TrendMapperTest {
    @Test
    fun `ReelDto toEntity() should map correctly`() {
        val entity = trendDto.toEntity()

        assertThat(entity.id).isEqualTo(trendDto.id)
        assertThat(entity.thumbnailUrl).isEqualTo(trendDto.trendImageUrl)
        assertThat(entity.videoUrl).isEqualTo(trendDto.videoUrl)
        assertThat(entity.description).isEqualTo(trendDto.description)
        assertThat(entity.likesCount).isEqualTo(trendDto.likesCount)
        assertThat(entity.viewsCount).isEqualTo(trendDto.viewsCount)
        val expectedCreatedAt = LocalDateTime(2025, 9, 25, 12, 0, 0)
        assertThat(entity.createdAt).isEqualTo(expectedCreatedAt)
    }

    @Test
    fun `ReelDTO with null values toEntity() should map to default values`() {
        val dto = TrendDto()
        val reel = dto.toEntity()

        assertThat(reel.id).isEqualTo("")
        assertThat(reel.thumbnailUrl).isEqualTo("")
        assertThat(reel.videoUrl).isEqualTo("")
        assertThat(reel.description).isEqualTo("")
        assertThat(reel.likesCount).isEqualTo(0)
        assertThat(reel.viewsCount).isEqualTo(0)
    }

    @Test
    fun `createdAt in DTO with null values toEntity() should return null`() {
        val dto = TrendDto()
        val reel = dto.toEntity()

        assertThat(reel.createdAt).isNull()
    }

    @Test
    fun `invalid createdAt string should map to null`() {
        val dto = TrendDto(createdAt = "invalid-date")
        val reel = dto.toEntity()

        assertThat(reel.createdAt).isNull()
    }

    private companion object {
        val trendDto = TrendDto(
            id = "1",
            trendImageUrl = "thumb.jpg",
            videoUrl = "video.mp4",
            description = "sample reel",
            likesCount = 10,
            viewsCount = 100,
            createdAt = "2025-09-25T12:00:00"
        )
        val categoriesDto = listOf(
            CategoryDto(id = "uuid 1", name = "Sport", emoji = "⚽"),
            CategoryDto(id = "uuid 2", name = "Music", emoji = "🎵")
        )
    }
}