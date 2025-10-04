package net.thechance.mena.wallet.repository

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl
import kotlin.coroutines.CoroutineContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class StatementRepositoryImplTest {

    private lateinit var statementRepository: StatementRepositoryImpl
    private lateinit var networkClient: NetworkClient
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        networkClient = mock<NetworkClient>(mode = MockMode.autofill)
        statementRepository = StatementRepositoryImpl(networkClient, CoroutineScope(testDispatcher))
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `TransactionsPdf should return statement when API call is successful`() = runTest(testDispatcher) {
        everySuspend { networkClient.get(any(), any()) } returns statementResonance

        val result = statementRepository.getTransactionsPdf()

        assertContentEquals(statement, result)
    }

    @Test
    fun `TransactionsPdf should return last statement when TransactionsPdf has been called before`() = runTest(testDispatcher) {
        everySuspend { networkClient.get(any(), any()) } returns statementResonance

        statementRepository.getTransactionsPdf()
        val result = statementRepository.getTransactionsPdf()

        assertContentEquals(statement, result)
    }

    @Test
    fun `TransactionsPdf should return cached statement`() = runTest(testDispatcher) {
        everySuspend { networkClient.get(any(), any()) } returns statementResonance

        statementRepository.getTransactionsPdf()
        statementRepository.getTransactionsPdf()

        verifySuspend(exactly(1)) { networkClient.get(any(), any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `statement should fetch new statement after expiration time`() = runTest(testDispatcher) {
        everySuspend { networkClient.get(any(), any()) } returns statementResonance

        statementRepository.getTransactionsPdf()
        advanceUntilIdle()
        statementRepository.getTransactionsPdf()

        verifySuspend(exactly(2)) { networkClient.get(any(), any()) }
    }

    @Test
    fun `getTransactionsPdf should return null when there is no cached statement`() = runTest(testDispatcher) {

        val result = statementRepository.getTransactionsPdf()

        assertContentEquals(null, result)
    }

    @Test
    fun `getTransactionsPdf should return cached statement`() = runTest(testDispatcher) {
        everySuspend { networkClient.get(any(), any()) } returns statementResonance

        statementRepository.getTransactionsPdf()
        val result = statementRepository.getTransactionsPdf()

        assertContentEquals(statement, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getTransactionsPdf should return null after expiration time`() = runTest(testDispatcher) {
        everySuspend { networkClient.get(any(), any()) } returns statementResonance

        statementRepository.getTransactionsPdf()
        val result = statementRepository.getTransactionsPdf()
        advanceUntilIdle()

        assertContentEquals(statement, result)
    }

    private val statementResonance = object : HttpResponse() {
        @InternalAPI
        override val rawContent: ByteReadChannel = ByteReadChannel(statement)
        override val call: HttpClientCall = HttpClientCall(HttpClient())
        override val version: HttpProtocolVersion = HttpProtocolVersion.HTTP_1_1
        override val requestTime: GMTDate = GMTDate()
        override val responseTime: GMTDate = GMTDate()
        override val coroutineContext: CoroutineContext = testDispatcher
        override val status: HttpStatusCode = HttpStatusCode.OK
        override val headers: Headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Pdf.toString())
        override fun toString(): String = "HttpResponse(status=$status, headers=$headers)"
    }

    private companion object {
        val statement = ByteArray(5, { it.toByte() })
    }
}
