package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.data.remote.dto.CategoryDto
import net.thechance.mena.trends.data.remote.dto.ReelDto
import net.thechance.mena.trends.data.remote.mapper.toEntity
import kotlin.test.Test

internal class ReelMapperTest {
    @Test
    fun `ReelDto toEntity() should map correctly`() {
        val entity = reelDto.toEntity()

        assertThat(entity.id).isEqualTo(reelDto.id)
        assertThat(entity.thumbnailUrl).isEqualTo(reelDto.reelImageUrl)
        assertThat(entity.videoUrl).isEqualTo(reelDto.videoUrl)
        assertThat(entity.description).isEqualTo(reelDto.description)
        assertThat(entity.likesCount).isEqualTo(reelDto.likesCount)
        assertThat(entity.viewsCount).isEqualTo(reelDto.viewsCount)
        val expectedCreatedAt = LocalDateTime(2025, 9, 25, 12, 0, 0)
        assertThat(entity.createdAt).isEqualTo(expectedCreatedAt)
    }

    @Test
    fun `ReelDTO with null values toEntity() should map to default values`() {
        val dto = ReelDto()
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
        val dto = ReelDto()
        val reel = dto.toEntity()

        assertThat(reel.createdAt).isNull()
    }

    @Test
    fun `invalid createdAt string should map to null`() {
        val dto = ReelDto(createdAt = "invalid-date")
        val reel = dto.toEntity()

        assertThat(reel.createdAt).isNull()
    }

    private companion object {
        val reelDto = ReelDto(
            id = "1",
            reelImageUrl = "thumb.jpg",
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