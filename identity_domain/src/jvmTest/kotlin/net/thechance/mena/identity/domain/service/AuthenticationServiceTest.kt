package net.thechance.mena.identity.domain.service

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationServiceTest {

    private val accessToken = "TheChance_accessToken"
    private val refreshToken = "TheChance_refreshToken"

    private val authenticationRepository: AuthenticationRepository = mockk()
    private val authorizationService = AuthorizationService(authenticationRepository)

    @Test
    fun `getAccessToken() should return access token`() = runTest{
        coEvery { authenticationRepository.getAccessToken() } returns accessToken

        val result = authorizationService.getAccessToken()

        assertEquals(accessToken ,result)

    }

    @Test
    fun `getRefreshToken() should return refresh token`() = runTest{
        coEvery { authenticationRepository.refreshAccessToken() } returns refreshToken

        val result = authorizationService.getNewAccessToken()

        assertEquals(refreshToken ,result)

    }

    @Test
    fun `observeTokenChange() should return refresh token`() = runTest{

        val fakeTokenFlow = MutableStateFlow(accessToken)

        coEvery { authenticationRepository.observeTokenChange() } returns fakeTokenFlow

        val result = authorizationService.observeAccessToken()

        assertEquals(accessToken, result.value)

        result.test {
            fakeTokenFlow.emit("newToken")
            assertEquals("newToken", result.value)
            cancelAndIgnoreRemainingEvents()
        }

    }







}