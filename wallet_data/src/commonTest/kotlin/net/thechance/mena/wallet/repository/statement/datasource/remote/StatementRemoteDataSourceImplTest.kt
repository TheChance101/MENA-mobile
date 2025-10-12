package net.thechance.mena.wallet.repository.statement.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.data.database.LocalStatement
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.datasource.remote.StatementRemoteDataSourceImpl
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class StatementRemoteDataSourceImplTest {

    lateinit var statementRemoteDataSourceImpl: StatementRemoteDataSourceImpl
    lateinit var networkClient: NetworkClient

    val fakeDao = FakeStatementDao()

    @Test
    fun `getStatementWithMetaData returns DTO with byte array when API call is successful with partial filters`() = runTest {
        networkClient = createNetworkClient(getRespond = successPdfResponse)
        statementRemoteDataSourceImpl = StatementRemoteDataSourceImpl(networkClient)

        val result = statementRemoteDataSourceImpl.getStatementWithMetaData(transactionFilterParams1)

        assertTrue(result.byteArray.contentEquals(pdf1))
        assertEquals(99.80, result.totalInflows)
        assertEquals(520.75, result.totalOutflows)
        assertEquals("2025-09-25", result.startDate)
        assertEquals("2025-10-06", result.endDate)
    }

    @Test
    fun `getStatementWithMetaData throws exception when API call fails with server error`() = runTest {
        networkClient = createNetworkClient(getRespond = errorResponse)
        statementRemoteDataSourceImpl = StatementRemoteDataSourceImpl(networkClient)

        assertFailsWith<UnknownException> {
            statementRemoteDataSourceImpl.getStatementWithMetaData(null)
        }
    }

    @Test
    fun `extract statement headers should extract statement successfully`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "PDF binary or placeholder content",
                status = HttpStatusCode.OK,
                headers = headersOf(
                    "X-Statement-Total-Inflows" to listOf("99.80"),
                    "X-Statement-Total-Outflows" to listOf("520.75"),
                    "X-Statement-Start-Date" to listOf("2025-09-25"),
                    "X-Statement-End-Date" to listOf("2025-10-06")
                )
            )
        }

        val client = HttpClient(mockEngine)

        val response = client.get("http://localhost/wallet/transactions/statement?startDate=2025-09-25")

        val inflows = response.headers["X-Statement-Total-Inflows"]
        val outflows = response.headers["X-Statement-Total-Outflows"]
        val startDate = response.headers["X-Statement-Start-Date"]
        val endDate = response.headers["X-Statement-End-Date"]

        assertEquals("99.80", inflows)
        assertEquals("520.75", outflows)
        assertEquals("2025-09-25", startDate)
        assertEquals("2025-10-06", endDate)
    }

    @Test
    fun `statementDao should insert and retrieve statement successfully`() = runTest {
        val localStatement = LocalStatement(
            id = 0,
            totalInflows = 99.80,
            totalOutflows = 520.75,
            startDate = "2025-09-25",
            endDate = "2025-10-06",
            fileName = "statement_2025-09-25_2025-10-06.pdf",
            createdAt = 0L
        )

        fakeDao.insertStatement(localStatement)

        val all = fakeDao.getAllStatement(limit = 1, offset = 0)

        val stored = all.first()
        assertEquals(99.80, stored.totalInflows)
        assertEquals(520.75, stored.totalOutflows)
        assertEquals("2025-09-25", stored.startDate)
        assertEquals("2025-10-06", stored.endDate)
    }

    companion object {
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
                        HttpHeaders.ContentType to listOf(ContentType.Application.Pdf.toString()),
                        "X-Statement-Total-Inflows" to listOf("99.80"),
                        "X-Statement-Total-Outflows" to listOf("520.75"),
                        "X-Statement-Start-Date" to listOf("2025-09-25"),
                        "X-Statement-End-Date" to listOf("2025-10-06")
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

    class FakeStatementDao : StatementDao {

        private val storage = mutableListOf<LocalStatement>()
        private var nextId = 1L

        override suspend fun insertStatement(localStatement: LocalStatement) {
            val newStatement = localStatement.copy(id = nextId++)
            storage.add(newStatement)
        }

        override suspend fun getAllStatement(limit: Int, offset: Int): List<LocalStatement> {
            return storage.drop(offset).take(limit)
        }

        override suspend fun deleteStatementById(id: Long) {
            val index = storage.indexOfFirst { it.id == id }
            if (index >= 0) {
                storage.removeAt(index)
            }
        }

        override suspend fun getStatementById(id: Long): LocalStatement {
            return storage.firstOrNull { it.id == id }
                ?: throw NoSuchElementException("Statement with id $id not found")
        }
    }
}