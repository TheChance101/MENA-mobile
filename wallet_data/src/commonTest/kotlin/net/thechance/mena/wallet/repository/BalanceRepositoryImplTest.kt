package net.thechance.mena.wallet.repository

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.balance.BalanceRepositoryImpl
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BalanceRepositoryImplTest {

    lateinit var balanceRepository: BalanceRepositoryImpl
    lateinit var networkClient: NetworkClient

    @Test
    fun `getBalance returns balance when API call is successful`() = runTest {
        networkClient = createNetworkClient(getRespond = balanceResonance)
        balanceRepository = BalanceRepositoryImpl(networkClient)

        val result = balanceRepository.getBalance()

        assertEquals(BALANCE, result)
    }

    @Test
    fun `getBalance throws exception when API call fails`() = runTest {
        networkClient = createNetworkClient(getRespond = errorResonance)
        balanceRepository = BalanceRepositoryImpl(networkClient)

        assertFailsWith<UnknownNetworkException> {
            balanceRepository.getBalance()
        }
    }

    private companion object {
        const val BALANCE = 150.75

        val balanceResonance: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
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
