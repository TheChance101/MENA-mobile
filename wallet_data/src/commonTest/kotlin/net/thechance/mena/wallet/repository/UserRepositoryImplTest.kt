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
import net.thechance.mena.wallet.data.repository.user.UserRepositoryImpl
import net.thechance.mena.wallet.domain.entity.User
import net.thechance.mena.wallet.repository.utils.createNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserRepositoryImplTest {
    lateinit var userRepository: UserRepositoryImpl
    lateinit var networkClient: NetworkClient

    @Test
    fun `getReceiverByTransactionId returns user when API call is successful`() = runTest()
    {
        networkClient = createNetworkClient(userResponse)
        userRepository = UserRepositoryImpl(networkClient)

        val result = userRepository.getReceiverByTransactionId(transactionId)

        assertEquals(receiver, result)
    }

    @Test
    fun `getReceiverByTransactionId returns throw exception when API call is fails`() = runTest()
    {
        networkClient = createNetworkClient(userErrorResponse)
        userRepository = UserRepositoryImpl(networkClient)

        assertFailsWith<Exception> {
            userRepository.getReceiverByTransactionId(transactionId)
        }
    }

    companion object {
        val transactionId = Uuid.random()
        const val receiverName = "username1"
        const val receiverImg = "userimg1.png"
        val receiver = User(
            name = receiverName,
            imgUrl = receiverImg
        )

        val userResponse: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData =
            {
                respond(
                    content = """
                    {
                        "ImageUrl": "$receiverImg",
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