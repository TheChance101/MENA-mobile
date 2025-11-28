@file:OptIn(ExperimentalCoroutinesApi::class)

package net.thechance.mena.wallet.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.capture.Capture.Companion.slot
import dev.mokkery.mock
import dev.mokkery.verify
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.balance.BalanceRepositoryImpl
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BalanceRepositoryImplTest {

    lateinit var balanceRepository: BalanceRepositoryImpl
    lateinit var networkClient: NetworkClient
    private val authenticationRepository: AuthenticationRepository = mock(MockMode.autofill)
    private val authorizationService: AuthorizationService =
        AuthorizationService(authenticationRepository)

    @BeforeTest
    fun setup() {
        every { authenticationRepository.observeTokenChange() } returns MutableStateFlow("fake-token")
    }

    @Test
    fun `getBalance returns balance when API call is successful`() = runTest {
        networkClient = createNetworkClient(getRespond = balanceResonance)
        balanceRepository = BalanceRepositoryImpl(networkClient, authorizationService)

        val result = balanceRepository.getBalance()

        assertEquals(BALANCE, result)
    }

    @Test
    fun `getBalance throws exception when API call fails`() = runTest {
        networkClient = createNetworkClient(getRespond = errorResonance)
        balanceRepository = BalanceRepositoryImpl(networkClient, authorizationService)

        assertFailsWith<UnknownNetworkException> {
            balanceRepository.getBalance()
        }
    }

    @Test
    fun `observeBalance fetches and emits balance when initial value is zero`() = runTest {
        networkClient = createNetworkClient(getRespond = balanceResonance)
        balanceRepository = BalanceRepositoryImpl(networkClient, authorizationService)

        val flow = balanceRepository.observeBalance()

        val emitted = flow.first { it == BALANCE }

        assertEquals(BALANCE, emitted)
    }

    @Test
    fun `getBalance updates balanceFlow`() = runTest {
        networkClient = createNetworkClient(getRespond = balanceResonance)
        balanceRepository = BalanceRepositoryImpl(networkClient, authorizationService)

        balanceRepository.getBalance()

        val emitted = balanceRepository.observeBalance().first()
        assertEquals(BALANCE, emitted)
    }


    @Test
    fun `observeBalance returns current value immediately when already set`() = runTest {
        networkClient = createNetworkClient(getRespond = balanceResonance)
        balanceRepository = BalanceRepositoryImpl(networkClient, authorizationService)

        val initial = balanceRepository.getBalance()
        assertEquals(BALANCE, initial)

        val flow = balanceRepository.observeBalance()
        val emitted = flow.first()

        assertEquals(BALANCE, emitted)
    }

    private companion object {
        const val BALANCE = 150.75

        val balanceResonance: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """{"balance": $BALANCE}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

        val errorResonance: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
            respond(
                content = """{"message": "Server error occurred"}""",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }
    }
}
