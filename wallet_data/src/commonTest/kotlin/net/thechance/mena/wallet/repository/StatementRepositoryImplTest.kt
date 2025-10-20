package net.thechance.mena.wallet.repository

import dev.mokkery.MockMode
import dev.mokkery.mock
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StatementRepositoryImplTest {

    private lateinit var statementRepository: StatementRepositoryImpl
    private lateinit var networkClient: NetworkClient
    private lateinit var statementDao: StatementDao
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        statementDao = mock<StatementDao>(mode = MockMode.autofill)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `getStatementWithMetadata should return statement with metadata when API call is successful`() =
        runTest(testDispatcher) {
            networkClient = createNetworkClient(getRespond = successPdfResponse)
            statementRepository = StatementRepositoryImpl(networkClient, statementDao)

            val result = statementRepository.getStatementWithMetadata()

            assertContentEquals(pdfBytes, result.byteArray)
            assertEquals(99.80, result.totalInflows)
            assertEquals(520.75, result.totalOutflows)
            assertEquals(LocalDate.parse("2025-09-25"), result.startDate)
            assertEquals(LocalDate.parse("2025-10-06"), result.endDate)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getStatementWithMetadata should fetch new data after expiration time`() = runTest(testDispatcher) {
        networkClient = createNetworkClient(getRespond = successPdfResponse)
        statementRepository = StatementRepositoryImpl(networkClient, statementDao)

        val firstResult = statementRepository.getStatementWithMetadata()
        advanceUntilIdle()

        val secondResult = statementRepository.getStatementWithMetadata()

        assertContentEquals(pdfBytes, firstResult.byteArray)
        assertContentEquals(pdfBytes, secondResult.byteArray)
    }

    private companion object {
        val pdfBytes = ByteArray(5) { it.toByte() }

        val successPdfResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
            respond(
                content = pdfBytes,
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf(ContentType.Application.Pdf.toString()),
                    "X-Statement-Total-Inflows" to listOf("99.80"),
                    "X-Statement-Total-Outflows" to listOf("520.75"),
                    "X-Statement-Start-Date" to listOf("2025-09-25"),
                    "X-Statement-End-Date" to listOf("2025-10-06")
                )
            )
        }
    }
}