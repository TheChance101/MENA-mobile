package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import net.thechance.mena.trends.data.dto.ProfileDto
import kotlin.test.Test

internal class ProfileMapperTest {

    @Test
    fun `profileDto toEntity() should map correctly`() {
        val profileDto = ProfileDto(
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.jpg",
            username = "nour"
        )
        val profile = profileDto.toEntity()

        assertThat(profile.firstName).isEqualTo("nour")
        assertThat(profile.lastName).isEqualTo("nour")
        assertThat(profile.profileImageUrl).isEqualTo("img.jpg")
        assertThat(profile.username).isEqualTo("nour")
    }

    @Test
    fun `profileDto with null profileImageUrl toEntity() should map to empty string`() {
        val profileDto = ProfileDto(
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = null,
            username = "nour"
        )
        val profile = profileDto.toEntity()

        assertThat(profile.firstName).isEqualTo("nour")
        assertThat(profile.lastName).isEqualTo("nour")
        assertThat(profile.profileImageUrl).isEmpty()
        assertThat(profile.username).isEqualTo("nour")
    }
}