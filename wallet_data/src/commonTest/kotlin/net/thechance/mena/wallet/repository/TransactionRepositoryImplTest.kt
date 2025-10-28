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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.transaction.TransactionRepositoryImpl
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.domain.exceptions.UnknownNetworkException
import net.thechance.mena.wallet.domain.model.TransactionReceiver
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
    fun `getTransactionReceiver returns user when API call is successful`() = runTest()
    {
        networkClient = createNetworkClient(userResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        val result = transactionRepository.getTransactionReceiver(transaction1Id)

        assertEquals(transactionReceiver, result)
    }

    @Test
    fun `getTransactionReceiver returns throw exception when API call is fails`() = runTest()
    {
        networkClient = createNetworkClient(userErrorResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        assertFailsWith<Exception> {
            transactionRepository.getTransactionReceiver(transaction1Id)
        }
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
            createdAt = LocalDateTime(
                date = LocalDate(2025, 8, 20),
                time = LocalTime(12, 0)
            ),
            amount = 5000.0,
            status = TransactionStatus.SUCCESS,
            senderName = "Nour Elhoda",
            receiverName = "Nour Elhoda",
            type = TransactionType.RECEIVED
        )

        val transactionResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """
                    {
                        "id": "$transaction1Id",
                        "senderName": "${transaction1.senderName}",
                        "receiverName": "${transaction1.receiverName}",
                        "status": "SUCCESS",
                        "type": "RECEIVED",
                        "createdAt": "2025-08-20T12:00",
                        "amount": ${transaction1.amount}
                    }
                    """,
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

        const val receiverName = "username1"
        const val receiverImg = "userimg1.png"
        val transactionReceiver = TransactionReceiver(
            name = receiverName,
            imgUrl = receiverImg
        )

        val userResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """
                    {
                        "imageUrl": "$receiverImg",
                        "name": "$receiverName"
                    }
                    """,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

        val userErrorResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """
                    {
                        "status": 500,
                        "message": "Server error occurred"
                    }
                    """.trimIndent(),
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }
    }
}