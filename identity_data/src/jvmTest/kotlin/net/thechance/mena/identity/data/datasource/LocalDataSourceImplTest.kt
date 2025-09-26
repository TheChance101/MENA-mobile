package net.thechance.mena.identity.data.datasource


import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.russhwolf.settings.Settings
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class LocalDataSourceImplTest {

    private val mockSettings: Settings = mockk(relaxed = true)
    private val localDataSource: LocalDataSourceImpl = LocalDataSourceImpl(mockSettings)

    @Test
    fun `saveAccessToken should store token in settings`() {
        // Given
        val accessToken = "test_access_token"
        every { mockSettings.putString(any(), any()) } just Runs

        // When
        localDataSource.saveAccessToken(accessToken)

        // Then
        verify { mockSettings.putString(LocalDataSourceImpl.ACCESS_TOKEN, accessToken) }
    }

    @Test
    fun `getAccessToken should return stored token from settings`() {
        // Given
        val expectedToken = "stored_access_token"
        every { mockSettings.getString(LocalDataSourceImpl.ACCESS_TOKEN, "") } returns expectedToken

        // When
        val result = localDataSource.getAccessToken()

        // Then
        assertThat(result).isEqualTo(result)
        verify { mockSettings.getString(LocalDataSourceImpl.ACCESS_TOKEN, "") }
    }

    @Test
    fun `getAccessToken should return empty string when no token stored`() {
        // Given
        every { mockSettings.getString(LocalDataSourceImpl.ACCESS_TOKEN, "") } returns ""

        // When
        val result = localDataSource.getAccessToken()

        // Then
        assertThat(result).isEmpty()
        verify { mockSettings.getString(LocalDataSourceImpl.ACCESS_TOKEN, "") }
    }

    @Test
    fun `saveRefreshToken should store token in settings`() {
        // Given
        val refreshToken = "test_refresh_token"
        every { mockSettings.putString(any(), any()) } just Runs

        // When
        localDataSource.saveRefreshToken(refreshToken)

        // Then
        verify { mockSettings.putString(LocalDataSourceImpl.REFRESH_TOKEN, refreshToken) }
    }

    @Test
    fun `getRefreshToken should return stored token from settings`() {
        // Given
        val expectedToken = "stored_refresh_token"
        every {
            mockSettings.getString(
                LocalDataSourceImpl.REFRESH_TOKEN,
                ""
            )
        } returns expectedToken

        // When
        val result = localDataSource.getRefreshToken()

        // Then
        assertThat(result).isEqualTo(expectedToken)
        verify { mockSettings.getString(LocalDataSourceImpl.REFRESH_TOKEN, "") }
    }

    @Test
    fun `getRefreshToken should return empty string when no token stored`() {
        // Given
        every { mockSettings.getString(LocalDataSourceImpl.REFRESH_TOKEN, "") } returns ""

        // When
        val result = localDataSource.getRefreshToken()

        // Then
        assertThat(result).isEmpty()
        verify { mockSettings.getString(LocalDataSourceImpl.REFRESH_TOKEN, "") }
    }
}