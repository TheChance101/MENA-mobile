@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.dto.local.LocalStatement
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class StatementRepositoryImplTest {

    private lateinit var statementRepository: StatementRepositoryImpl
    private lateinit var networkClient: NetworkClient
    private lateinit var statementDao: StatementDao
    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock()

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
            statementRepository = StatementRepositoryImpl(networkClient, userRepository,statementDao)

            val result = statementRepository.getStatementWithMetadata()

            assertContentEquals(pdfBytes, result.byteArray)
            assertEquals(99.80, result.totalInflows)
            assertEquals(520.75, result.totalOutflows)
            assertEquals(LocalDate.parse("2025-09-25"), result.startDate)
            assertEquals(LocalDate.parse("2025-10-06"), result.endDate)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getStatementWithMetadata should fetch new data after expiration time`() =
        runTest(testDispatcher) {
            networkClient = createNetworkClient(getRespond = successPdfResponse)
            statementRepository = StatementRepositoryImpl(networkClient, userRepository,statementDao)

            val firstResult = statementRepository.getStatementWithMetadata()
            advanceUntilIdle()

            val secondResult = statementRepository.getStatementWithMetadata()

            assertContentEquals(pdfBytes, firstResult.byteArray)
            assertContentEquals(pdfBytes, secondResult.byteArray)
        }

    @Test
    fun `getStatements should return statements from dao successfully`() = runTest(testDispatcher) {
        networkClient = createNetworkClient()
        statementDao = mock(mode = MockMode.autofill)

        val mockStatements = listOf(localStatement)

        everySuspend {
            statementDao.getAllStatement(userId = any(), limit = 10, offset = 0)
        } returns mockStatements

        everySuspend { userRepository.getUser() } returns user

        statementRepository = StatementRepositoryImpl(networkClient, userRepository, statementDao)

        val result = statementRepository.getStatements(page = 1, pageSize = 10)

        assertEquals(mockStatements.size, result.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getStatements should throw Exception when dao throws`() = runTest(testDispatcher) {
        // arrange
        networkClient = createNetworkClient()
        statementDao = mock(mode = MockMode.autofill)
        everySuspend {
            statementDao.getAllStatement(
                limit = any(),
                offset = any(),
                userId = "123"
            )
        } throws Exception("db error")
        statementRepository = StatementRepositoryImpl(networkClient, userRepository,statementDao)

        // act & assert
        assertFailsWith<Exception> {
            statementRepository.getStatements(page = 1, pageSize = 10)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `insertStatement should throw Exception when dao throws`() =
        runTest(testDispatcher) {
            // arrange
            networkClient = createNetworkClient()
            statementDao = mock(mode = MockMode.autofill)
            everySuspend { statementDao.insertStatement(any()) } throws Exception("insert failed")
            statementRepository = StatementRepositoryImpl(networkClient, userRepository,statementDao)

            // act & assert
            assertFailsWith<Exception> {
                statementRepository.insertStatement(testStatement())
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `deleteStatementById should throw Exception when dao throws`() =
        runTest(testDispatcher) {
            // arrange
            networkClient = createNetworkClient()
            statementDao = mock(mode = MockMode.autofill)
            everySuspend { statementDao.deleteStatementById(any()) } throws Exception("delete failed")
            statementRepository = StatementRepositoryImpl(networkClient, userRepository,statementDao)

            // act & assert
            assertFailsWith<Exception> {
                statementRepository.deleteStatementById(Uuid.random())
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getStatementById should throw Exception when dao throws`() =
        runTest(testDispatcher) {
            // arrange
            networkClient = createNetworkClient()
            statementDao = mock(mode = MockMode.autofill)
            everySuspend { statementDao.getStatementById(any()) } throws Exception("get by id failed")
            statementRepository = StatementRepositoryImpl(networkClient, userRepository,statementDao)

            // act & assert
            assertFailsWith<Exception> {
                statementRepository.getStatementById(Uuid.random())
            }
        }

    @OptIn(ExperimentalTime::class)
    private fun testStatement() = Statement(
        id = Uuid.random(),
        startDate = LocalDate.parse("2025-01-01"),
        endDate = LocalDate.parse("2025-01-31"),
        totalInflows = 100.0,
        totalOutflows = 50.0,
        fileName = "test-statement.pdf"
    )

    private val localStatement = LocalStatement(
        id = Uuid.random().toString(),
        startDate ="2025-01-01",
        endDate = "2025-01-31",
        totalInflows = 100.0,
        totalOutflows = 50.0,
        fileName = "test-statement.pdf",
        userId = "1"
    )

    val user = flowOf(
        User(
            id = Uuid.random(),
            firstName = "Ahmed",
            lastName = "Sayed",
            profileImageUrl = "https://example.com/profile.jpg",
            username = "Ahmed123",
            birthDate = LocalDate(1995, 11, 15),
            gender = Gender.MALE
        )
    )

    private companion object {
        val pdfBytes = ByteArray(5) { it.toByte() }

        val successPdfResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
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