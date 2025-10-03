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
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
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

        val result = transactionRepository.getTransactionHistory(PAGE,PAGE_SIZE,null)
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

        assertFailsWith<UnknownException> {
            transactionRepository.getTransactionHistory(PAGE,PAGE_SIZE,null)
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
                      "transactions": [
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

        val transactionErrorResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
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