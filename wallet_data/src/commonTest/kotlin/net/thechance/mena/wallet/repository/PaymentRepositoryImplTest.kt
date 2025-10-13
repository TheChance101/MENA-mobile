@file:OptIn(ExperimentalUuidApi::class)

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
import net.thechance.mena.wallet.data.repository.payment.PaymentRepositoryImpl
import net.thechance.mena.wallet.domain.model.PaymentConfirmation
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PaymentRepositoryImplTest {
    lateinit var paymentRepository: PaymentRepositoryImpl
    lateinit var networkClient: NetworkClient

    @Test
    fun `getPaymentConfirmation returns PaymentConfirmation when API call is successful`() = runTest()
    {
        networkClient = createNetworkClient(postRespond = paymentConfirmation1SuccessResponse)
        paymentRepository = PaymentRepositoryImpl(networkClient)

        val result = paymentRepository.getPaymentConfirmation(receiver1Id,amount1)

        assertEquals(paymentConfirmation1, result)
    }

    @Test
    fun `getPaymentConfirmation throw exception when API call is fails`() = runTest()
    {
        networkClient = createNetworkClient(postRespond = paymentConfirmationErrorResponse)
        paymentRepository = PaymentRepositoryImpl(networkClient)

        assertFailsWith<Exception> {
            paymentRepository.getPaymentConfirmation(receiver1Id,amount1)
        }
    }
    @Test
    fun `submitTransaction completes successfully when API call is successful`() = runTest {
        networkClient = createNetworkClient(postRespond = { request ->
            respond(
                content = "",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        })
        paymentRepository = PaymentRepositoryImpl(networkClient)

        paymentRepository.submitTransaction(Uuid.random())
    }

    @Test
    fun `submitTransaction throws exception when API call fails`() = runTest {
        networkClient = createNetworkClient(postRespond = { request ->
            respond(
                content = "",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        })
        paymentRepository = PaymentRepositoryImpl(networkClient)

        assertFailsWith<Exception> {
            paymentRepository.submitTransaction(Uuid.random())
        }
    }
    private companion object {
        val receiver1Id = Uuid.random()
        const val balance1 = 500045.0102
        const val amount1 = 5000.0
        const val receiverName1 = "nour"
        const val valid1 = true
        const val receiverImg1 = "https://media.istockphoto.com/id/469738422/photo/large-boulders-on-lake-shore-at-sunset-minnesota-usa.jpg?s=612x612&w=0&k=20&c=4FzViDygZ8CgixTqt3VOudLJUP8uoSeh2UlD_qHYkAw="

        val paymentConfirmation1 = PaymentConfirmation(
            balance = balance1,
            receiverName = receiverName1,
            receiverImg = receiverImg1,
            status = true
        )

        val paymentConfirmation1SuccessResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """
                    {
                        "isValid": $valid1,
                        "currentBalance": $balance1,
                        "receiverName": "$receiverName1",
                        "receiverImageUrl": "$receiverImg1"
                    }
                    """,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }

        val paymentConfirmationErrorResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
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