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
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals

class StatementRepositoryImplTest {

    lateinit var statementRepository: StatementRepositoryImpl
    lateinit var networkClient: NetworkClient

    @Test
    fun `getStatement should return statement when API call is successful`() = runTest {
        networkClient = createNetworkClient(statementResonance)
        statementRepository = StatementRepositoryImpl(networkClient)

        val result = statementRepository.getStatement()

        assertEquals(result.contentEquals(statement), true)
    }

    @Test
    fun `getLastStatement should return null if getStatement has not been called`() = runTest {
        networkClient = createNetworkClient(statementResonance)
        statementRepository = StatementRepositoryImpl(networkClient)

        val result = statementRepository.getLastStatement()

        assertEquals(result, null)
    }

    @Test
    fun `getLastStatement should return last statement when getStatement has been called`() = runTest {
        networkClient = createNetworkClient(statementResonance)
        statementRepository = StatementRepositoryImpl(networkClient)

        statementRepository.getStatement()
        val result = statementRepository.getLastStatement()

        assertEquals(result.contentEquals(statement), true)
    }

    private companion object {
        val statement = ByteArray(5, { it.toByte() })

        val statementResonance: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
            respond(
                content = statement,
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Pdf.toString()
                )
            )
        }
    }
}
