package net.thechance.mena.wallet.repository.statement.datasource.remote

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.datasource.remote.StatementRemoteDataSourceImpl
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StatementRemoteDataSourceImplTest {

    lateinit var statementRemoteDataSourceImpl: StatementRemoteDataSourceImpl
    lateinit var networkClient: NetworkClient

    @Test
    fun `getTransactionPdf returns byte array when API call is successful with partial filters`() = runTest {
        networkClient = createNetworkClient(getRespond = successPdfResponse)
        statementRemoteDataSourceImpl = StatementRemoteDataSourceImpl(networkClient)

        val result = statementRemoteDataSourceImpl.getTransactionPdf(transactionFilterParams1)

        assertTrue(result.contentEquals(pdf1))
    }

    @Test
    fun `getTransactionPdf throws exception when API call fails with server error`() = runTest {
        networkClient = createNetworkClient(getRespond = errorResponse)
        statementRemoteDataSourceImpl = StatementRemoteDataSourceImpl(networkClient)

        assertFailsWith<UnknownException> {
            statementRemoteDataSourceImpl.getTransactionPdf(null)
        }
    }

    companion object{
        val transactionFilterParams1 = TransactionFilterParams(
            types = listOf(TransactionType.SENT),
            status = TransactionStatus.SUCCESS,
            startDate = LocalDate(2025, 8, 20),
            endDate = LocalDate(2025, 8, 30)
        )

        val pdf1 = ByteArray(12)

        val successPdfResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = pdf1,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Pdf.toString(),
                    )
                )
            }

        val errorResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
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