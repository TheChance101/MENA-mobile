@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.repository

import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.payment.PaymentRepositoryImpl
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PaymentRepositoryImplTest {
    lateinit var paymentRepository: PaymentRepositoryImpl
    lateinit var networkClient: NetworkClient

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
}