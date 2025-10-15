package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.data.dto.CategoryDto
import net.thechance.mena.trends.data.dto.ReelDto
import net.thechance.mena.trends.domain.entity.Category
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

    @Test
    fun `ReelDTO with null categories should map to empty list`() {
        val dto = ReelDto(categories = null)

        val reel = dto.toEntity()

        assertThat(reel.categories).isEqualTo(emptyList())
    }

    @Test
    fun `ReelDTO with valid categories should map correctly`() {
        val dto = ReelDto(categories = categoriesDto)

        val reel = dto.toEntity()

        assertThat(reel.categories.first().id).isEqualTo(categoriesDto.first().id)
        assertThat(reel.categories.first().name).isEqualTo(categoriesDto.first().name)
        assertThat(reel.categories.first().emoji).isEqualTo(categoriesDto.first().emoji)

        assertThat(reel.categories.last().id).isEqualTo(categoriesDto.last().id)
        assertThat(reel.categories.last().name).isEqualTo(categoriesDto.last().name)
        assertThat(reel.categories.last().emoji).isEqualTo(categoriesDto.last().emoji)

        assertThat(reel.categories.size).isEqualTo(2)
    }

    @Test
    fun `ReelDTO with some invalid categories should ignore nulls`() {
        val dto = ReelDto(
            categories = listOf(
                CategoryDto(id = "1", name = "Comedy", emoji = "⚽"),
                CategoryDto(id = "3", name = null, emoji = null),
                CategoryDto(id = null, name = "NoId", emoji = null)
            )
        )

        val reel = dto.toEntity()

        assertThat(reel.categories.size).isEqualTo(2)
        assertThat(reel.categories.first()).isEqualTo(Category("1", "Comedy", "⚽"))
        assertThat(reel.categories.last()).isEqualTo(Category("3", "", ""))
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