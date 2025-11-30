@file:OptIn(ExperimentalTime::class)

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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.transaction.TransactionRepositoryImpl
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TransactionRepositoryImplTest {
    lateinit var transactionRepository: TransactionRepositoryImpl
    lateinit var networkClient: NetworkClient

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `getTransactionsHistory returns transactions when API call is successful`() = runTest()
    {
        networkClient = createNetworkClient(getRespond = successResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        val result = transactionRepository.getTransactionHistory(PAGE, PAGE_SIZE, null)
        assertEquals(1, result.size)
        val transaction: Transaction = result.first()
        assertEquals(EXPECTED_AMOUNT, transaction.amount)
        assertEquals(TransactionStatus.SUCCESS, transaction.status)
        assertEquals(TransactionType.SENT, transaction.type)

    }

    @Test
    fun `getTransactionHistory throws exception when API call fails`() = runTest {

        networkClient = createNetworkClient(getRespond = errorResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        assertFailsWith<UnknownNetworkException> {
            transactionRepository.getTransactionHistory(PAGE, PAGE_SIZE, null)
        }
    }

    @Test
    fun `getTransactionById returns transaction when API call is successful`() = runTest()
    {
        networkClient = createNetworkClient(transactionResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        val result = transactionRepository.getTransactionById(transaction1Id)

        assertEquals(transaction1, result)
    }

    @Test
    fun `getTransactionById returns throw exception when API call is fails`() = runTest()
    {
        networkClient = createNetworkClient(transactionErrorResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        assertFailsWith<Exception> {
            transactionRepository.getTransactionById(transaction1Id)
        }
    }

    @Test
    fun `addPendingTransaction returns transactionId when API call is successful`() = runTest()
    {
        networkClient = createNetworkClient(postRespond = addPendingTransactionSuccessResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        val result = transactionRepository.addPendingTransaction(
            receiverId = receiverId1,
            amount = amount1
        )

        assertEquals(transaction1Id, result)
    }

    @Test
    fun `getPaymentConfirmation throw exception when API call is fails`() = runTest()
    {
        networkClient = createNetworkClient(postRespond = addPendingTransactionErrorResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        assertFailsWith<Exception> {
            transactionRepository.addPendingTransaction(
                receiverId = receiverId1,
                amount = amount1
            )
        }
    }

    @Test
    fun `submitTransaction succeeds when API call is successful`() = runTest {
        val submitSuccessResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
            respond(
                content = "",
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }

        networkClient = createNetworkClient(postRespond = submitSuccessResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        transactionRepository.submitTransaction(transaction1Id)
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val PAGE = 1
        const val EXPECTED_ID = "550e8400-e29b-41d4-a716-446655440000"
        const val EXPECTED_SENDER = "Alice"
        const val EXPECTED_RECEIVER = "Bob"
        const val EXPECTED_AMOUNT = 250.0

        val successResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content =
                        """
                    {
                      "totalElements": 1,
                      "page": 0,
                      "pageSize": 10,
                      "totalPages": 1,
                      "items": [
                        {
                          "id": "$EXPECTED_ID",
                          "senderName": "$EXPECTED_SENDER",
                          "receiverName": "$EXPECTED_RECEIVER",
                          "status": "SUCCESS",
                          "type": "SENT",
                          "createdAt": "2025-09-29T12:00:00",
                          "amount": $EXPECTED_AMOUNT
                        }
                      ]
                    }
                    """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
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

        val transaction1Id = Uuid.random()
        val transaction1 = Transaction(
            id = transaction1Id,
            createdAt = Instant.parse("2025-08-20T12:00:00Z")
                .toLocalDateTime(TimeZone.currentSystemDefault()),
            amount = 5000.0,
            status = TransactionStatus.SUCCESS,
            senderName = "Nour Elhoda",
            receiverName = "Nour Elhoda",
            senderImageUrl = "",
            receiverImageUrl = "",
            type = TransactionType.RECEIVED
        )

        val transactionResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """
                    {
                     "id": "$transaction1Id",
                     "sender": {
                         "name": "${transaction1.senderName}",
                         "imageUrl": ""
                     },
                     "receiver": {
                         "name": "${transaction1.receiverName}",
                         "imageUrl": ""
                     },
                     "status": "SUCCESS",
                     "type": "RECEIVED",
                     "createdAt": "2025-08-20T12:00:00",
                     "amount": ${transaction1.amount}
                    }
                    """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

        val transactionErrorResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """{"message": "Server error occurred"}""",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

        const val amount1 = 5000.0
        val receiverId1 = Uuid.random()

        val addPendingTransactionSuccessResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = "$transaction1Id",
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

        val addPendingTransactionErrorResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """
                    {
                    "status": 500,
                    "message": "Server error occurred"
                    }
                    """.trimMargin(),
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }
    }
}