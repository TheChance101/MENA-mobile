package net.thechance.mena.identity.data.mapper

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.data.dto.auth.response.AuthenticationResponse
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.model.RegisterRequest
import org.junit.Test
import kotlin.test.assertEquals

class RegisterMapperTest {

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with correct phone number`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals("+9647901234567", result.phoneNumber)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with correct username`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals("testuser", result.username)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with correct firstName`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals("John", result.firstName)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with correct lastName`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals("Doe", result.lastName)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with correct birthDate`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals("2000-01-01", result.birthDate)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with MALE gender`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals(1, result.gender)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with FEMALE gender`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "Jane",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.FEMALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals(2, result.gender)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with correct password`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals("Password123", result.password)
    }

    @Test
    fun `toDto should map RegisterRequest to RegisterRequestDto with correct sessionId`() {
        val registerRequest = RegisterRequest(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            username = "testuser",
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE,
            password = "Password123"
        )
        val sessionId = "session123"

        val result = registerRequest.toDto(sessionId)

        assertEquals("session123", result.sessionId)
    }

    @Test
    fun `toDomain should map AuthenticationResponse to AuthenticationTokens with correct accessToken`() {
        val authResponse = AuthenticationResponse(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )

        val result = authResponse.toDomain()

        assertEquals("test_access_token", result.accessToken)
    }

    @Test
    fun `toDomain should map AuthenticationResponse to AuthenticationTokens with correct refreshToken`() {
        val authResponse = AuthenticationResponse(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )

        val result = authResponse.toDomain()

        assertEquals("test_refresh_token", result.refreshToken)
    }
}
