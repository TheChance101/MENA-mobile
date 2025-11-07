package net.thechance.mena.identity.data.mapper

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dto.profile.response.ProfileResponseDto
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UserMapperTest {

    @Test
    fun `ProfileResponseDto toDomain should map id correctly`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals(Uuid.parse("550e8400-e29b-41d4-a716-446655440000"), result.id)
    }

    @Test
    fun `ProfileResponseDto toDomain should map firstName correctly`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals("John", result.firstName)
    }

    @Test
    fun `ProfileResponseDto toDomain should map lastName correctly`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals("Doe", result.lastName)
    }

    @Test
    fun `ProfileResponseDto toDomain should map username to lowercase`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "JohnDoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals("johndoe", result.username)
    }

    @Test
    fun `ProfileResponseDto toDomain should normalize imageUrl`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = "https://example.com//image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals("https://example.com/image.jpg", result.profileImageUrl)
    }

    @Test
    fun `ProfileResponseDto toDomain should map birthDate correctly`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals(LocalDate(2000, 1, 1), result.birthDate)
    }

    @Test
    fun `ProfileResponseDto toDomain should map MALE gender correctly`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals(Gender.MALE, result.gender)
    }

    @Test
    fun `ProfileResponseDto toDomain should map FEMALE gender correctly`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "Jane",
            lastName = "Doe",
            username = "janedoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.FEMALE
        )

        val result = profileDto.toDomain()

        assertEquals(Gender.FEMALE, result.gender)
    }

    @Test
    fun `ProfileResponseDto toDomain should handle null imageUrl`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = null,
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toDomain()

        assertEquals("", result.profileImageUrl)
    }

    @Test
    fun `ProfileResponseDto toEntity should map id correctly`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toEntity()

        assertEquals("550e8400-e29b-41d4-a716-446655440000", result.id)
    }

    @Test
    fun `ProfileResponseDto toEntity should map username to lowercase`() {
        val profileDto = ProfileResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "JohnDoe",
            imageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = profileDto.toEntity()

        assertEquals("johndoe", result.username)
    }

    @Test
    fun `UserEntity toDomain should map id correctly`() {
        val userEntity = UserEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = userEntity.toDomain()

        assertEquals(Uuid.parse("550e8400-e29b-41d4-a716-446655440000"), result.id)
    }

    @Test
    fun `UserEntity toDomain should map username to lowercase`() {
        val userEntity = UserEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "JohnDoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = userEntity.toDomain()

        assertEquals("johndoe", result.username)
    }

    @Test
    fun `UserEntity toDomain should map MALE gender correctly`() {
        val userEntity = UserEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.MALE
        )

        val result = userEntity.toDomain()

        assertEquals(Gender.MALE, result.gender)
    }

    @Test
    fun `UserEntity toDomain should map FEMALE gender correctly`() {
        val userEntity = UserEntity(
            id = "550e8400-e29b-41d4-a716-446655440000",
            firstName = "Jane",
            lastName = "Doe",
            username = "janedoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = "2000-01-01",
            gender = UserEntity.FEMALE
        )

        val result = userEntity.toDomain()

        assertEquals(Gender.FEMALE, result.gender)
    }

    @Test
    fun `User toEntity should map id correctly`() {
        val user = User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )

        val result = user.toEntity()

        assertEquals("550e8400-e29b-41d4-a716-446655440000", result.id)
    }

    @Test
    fun `User toEntity should map username to lowercase`() {
        val user = User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            firstName = "John",
            lastName = "Doe",
            username = "JohnDoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )

        val result = user.toEntity()

        assertEquals("johndoe", result.username)
    }

    @Test
    fun `User toEntity should map birthDate to string format`() {
        val user = User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )

        val result = user.toEntity()

        assertEquals("2000-01-01", result.birthDate)
    }

    @Test
    fun `User toEntity should map MALE gender correctly`() {
        val user = User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            firstName = "John",
            lastName = "Doe",
            username = "johndoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )

        val result = user.toEntity()

        assertEquals(UserEntity.MALE, result.gender)
    }

    @Test
    fun `User toEntity should map FEMALE gender correctly`() {
        val user = User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            firstName = "Jane",
            lastName = "Doe",
            username = "janedoe",
            profileImageUrl = "https://example.com/image.jpg",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.FEMALE
        )

        val result = user.toEntity()

        assertEquals(UserEntity.FEMALE, result.gender)
    }
}