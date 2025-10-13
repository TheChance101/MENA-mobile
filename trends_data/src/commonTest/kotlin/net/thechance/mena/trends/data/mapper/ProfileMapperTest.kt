package net.thechance.mena.trends.data.mapper

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import net.thechance.mena.trends.data.dto.UserInfoDto
import kotlin.test.Test

internal class ProfileMapperTest {

    @Test
    fun `profileDto toEntity() should map correctly`() {

        val profile = userInfoDto[0].toEntity()

        assertThat(profile.firstName).isEqualTo("nour")
        assertThat(profile.lastName).isEqualTo("nour")
        assertThat(profile.profileImageUrl).isEqualTo("img.jpg")
        assertThat(profile.username).isEqualTo("nour")
    }

    @Test
    fun `profileDto with null  toEntity() should map to empty string`() {

        val profile = userInfoDto[1].toEntity()

        assertThat(profile.firstName).isEmpty()
        assertThat(profile.lastName).isEmpty()
        assertThat(profile.profileImageUrl).isEmpty()
        assertThat(profile.username).isEmpty()
    }
    private companion object {
        val userInfoDto = listOf(
            UserInfoDto(
                firstName = "nour",
                lastName = "nour",
                profileImageUrl = "img.jpg",
                username = "nour"
            ),
            UserInfoDto(
                firstName = null,
                lastName = null,
                profileImageUrl = null,
                username = null
            )

        )
    }
}