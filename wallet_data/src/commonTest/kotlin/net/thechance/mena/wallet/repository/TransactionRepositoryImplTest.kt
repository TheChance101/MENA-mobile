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
import net.thechance.mena.wallet.data.repository.balance.TransactionRepositoryImpl
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi

class TransactionRepositoryImplTest {
    lateinit var transactionRepository: TransactionRepositoryImpl
    lateinit var networkClient: NetworkClient

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `getTransactionsHistory returns transactions when API call is successful`() = runTest()
    {
        networkClient = createNetworkClient(successResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        val result = transactionRepository.getTransactionHistory(null)
        assertEquals(1, result.size)
        val transaction: Transaction = result.first()
        assertEquals(EXPECTED_AMOUNT, transaction.amount)
        assertEquals(TransactionStatus.SUCCESS, transaction.status)
        assertEquals(TransactionType.SENT, transaction.type)

    }

    @Test
    fun `getTransactionHistory throws exception when API call fails`() = runTest {

        networkClient = createNetworkClient(errorResponse)
        transactionRepository = TransactionRepositoryImpl(networkClient)

        assertFailsWith<UnknownException> {
            transactionRepository.getTransactionHistory(null)
        }
    }


    private companion object {
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
    }
}