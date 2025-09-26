package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.datasource.AuthRemoteDataSource
import net.thechance.mena.identity.data.datasource.LocalDataSource
import net.thechance.mena.identity.data.dto.auth.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.LoginResponseDto
import net.thechance.mena.identity.data.dto.auth.RefreshRequestDto
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.exception.UnknownException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import org.junit.Test

class AuthenticationRepositoryImplTest {

    private val authRemoteDataSource = mockk<AuthRemoteDataSource>()
    private val localDataSource = mockk<LocalDataSource>(relaxed = true)
    private val authenticationRepository = AuthenticationRepositoryImpl(
        authRemoteDataSource, localDataSource
    )

    @Test
    fun `login should call remote service with correct credentials when successful`() = runTest {
        val mobileNumber = "0123456789"
        val passWord = "testpassword"
        val countryCoed = "+20"
        coEvery { authRemoteDataSource.login(any()) } returns fakeLoginResponse

        // When
        authenticationRepository.login(countryCoed, mobileNumber, passWord)

        // Then
        coVerify {
            authRemoteDataSource.login(
                LoginRequestDto(
                    countryCoed + mobileNumber,
                    passWord
                )
            )
        }
    }

    @Test
    fun `login should save access token to local source when successful`() = runTest {
        // Given
        val mobileNumber = "0123456789"
        val passWord = "testpassword"
        val countryCoed = "+20"
        coEvery { authRemoteDataSource.login(any()) } returns fakeLoginResponse

        // When
        authenticationRepository.login(countryCoed, mobileNumber, passWord)

        // Then
        coVerify { localDataSource.saveAccessToken(fakeLoginResponse.accessToken) }
    }

    @Test
    fun `login should save refresh token to local source when successful`() = runTest {
        // Given
        val mobileNumber = "0123456789"
        val passWord = "testpassword"
        val countryCoed = "+20"
        coEvery { authRemoteDataSource.login(any()) } returns fakeLoginResponse

        // When
        authenticationRepository.login(countryCoed, mobileNumber, passWord)

        // Then
        coVerify { localDataSource.saveRefreshToken(fakeLoginResponse.refreshToken) }
    }

    @Test
    fun `getToken should save access token after successful refresh`() = runTest {
        // Given
        val oldRefreshToken = "old_refresh_token"
        coEvery { localDataSource.getRefreshToken() } returns oldRefreshToken
        coEvery { authRemoteDataSource.refreshToken(RefreshRequestDto(oldRefreshToken)) } returns fakeLoginResponse
        coEvery { localDataSource.getAccessToken() } returns "invalid_or_expired_token"

        // When
        authenticationRepository.refreshAccessToken()

        // Then
        coVerify { localDataSource.saveAccessToken(fakeLoginResponse.accessToken) }
    }

    @Test
    fun `getToken should save refresh token after successful refresh`() = runTest {
        // Given
        val oldRefreshToken = "old_refresh_token"
        coEvery { localDataSource.getRefreshToken() } returns oldRefreshToken
        coEvery { authRemoteDataSource.refreshToken(RefreshRequestDto(oldRefreshToken)) } returns fakeLoginResponse
        coEvery { localDataSource.getAccessToken() } returns "invalid_or_expired_token"

        // When
        authenticationRepository.refreshAccessToken()

        // Then
        coVerify { localDataSource.saveRefreshToken(fakeLoginResponse.refreshToken) }
    }

    @Test
    fun `getToken should return new access token after successful refresh`() = runTest {
        // Given
        val oldRefreshToken = "old_refresh_token"
        coEvery { localDataSource.getRefreshToken() } returns oldRefreshToken
        coEvery { authRemoteDataSource.refreshToken(RefreshRequestDto(oldRefreshToken)) } returns fakeLoginResponse
        coEvery { localDataSource.getAccessToken() } returns fakeLoginResponse.accessToken

        // When
        val result = authenticationRepository.getAccessToken()

        // Then
        assertEquals(fakeLoginResponse.accessToken, result)
    }

    @Test
    fun `login should throw UnAuthorizedException when remote returns 401`() = runTest {
        // Given
        val clientException = ClientRequestException(
            response = mockk(relaxed = true) {
                every { status } returns HttpStatusCode.Unauthorized
            },
            cachedResponseText = "Unauthorized"
        )
        coEvery { authRemoteDataSource.login(any()) } throws clientException

        // When & Then
        assertFailure {
            authenticationRepository.login("+20", "01234567", "password123")
        }.isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `login should throw InvalidCredentialsException when remote returns 404`() = runTest {
        // Given
        val clientException = ClientRequestException(
            response = mockk(relaxed = true) {
                every { status } returns HttpStatusCode.NotFound
            },
            cachedResponseText = "Not Found"
        )
        coEvery { authRemoteDataSource.login(any()) } throws clientException

        // When & Then
        assertFailure {
            authenticationRepository.login("+20", "01234567", "password123")
        }.isInstanceOf<InvalidCredentialsException>()

    }

    @Test
    fun `login should throw UserIsBlockedException when remote returns 403`() = runTest {
        // Given
        val clientException = ClientRequestException(
            response = mockk(relaxed = true) {
                every { status } returns HttpStatusCode.Forbidden
            },
            cachedResponseText = "Forbidden"
        )
        coEvery { authRemoteDataSource.login(any()) } throws clientException

        // When & Then
        assertFailure {
            authenticationRepository.login("+20", "01234567", "password123")
        }.isInstanceOf<UserIsBlockedException>()
    }


    @Test
    fun `getToken should throw UnAuthorizedException when remote returns 401`() = runTest {
        // Given
        val clientException = ClientRequestException(
            response = mockk(relaxed = true) {
                every { status } returns HttpStatusCode.Unauthorized
            },
            cachedResponseText = "Unauthorized"
        )
        coEvery { authRemoteDataSource.refreshToken(any()) } throws clientException

        // When & Then
        assertFailure {
            authenticationRepository.refreshAccessToken()
        }.isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `login should throw UnknownException when remote returns other error`() = runTest {
        // Given
        val clientException = ClientRequestException(
            response = mockk(relaxed = true) {
                every { status } returns HttpStatusCode.InternalServerError
            },
            cachedResponseText = "Internal Server Error"
        )
        coEvery { authRemoteDataSource.login(any()) } throws clientException

        assertFailure {
            authenticationRepository.login("+20", "01234567", "password123")
        }.isInstanceOf<UnknownException>()
    }

}

val fakeLoginResponse = LoginResponseDto(
    accessToken = "fake_access_token",
    refreshToken = "fake_refresh_token"
)
