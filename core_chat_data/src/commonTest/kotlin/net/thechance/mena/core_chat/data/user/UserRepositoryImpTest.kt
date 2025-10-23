package net.thechance.mena.core_chat.data.user

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.verify
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.createHttpClient
import net.thechance.mena.core_chat.data.jsonHeaders
import net.thechance.mena.core_chat.data.repository.UserRepositoryImpl
import net.thechance.mena.core_chat.data.source.remote.dto.UserDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.domain.exception.NotFoundException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UserRepositoryImpTest {

    @Test
    fun `should get user info from remote source when getUserInfo is called`() = runTest {

        val httpClient = createHttpClient(
            userResponse = {
                respond(
                    content = Json.encodeToString(UserDto.serializer(), user),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }
        )

        val userRepository = UserRepositoryImpl(
            client = httpClient,
        )

        val result = userRepository.getUserInfo()
        assertThat(result).isEqualTo(user.toDomain())
    }

    @Test
    fun `should throw NotFoundException when getUserInfo returns 404`() = runTest {

        val httpClient = createHttpClient(
            userResponse = {
                respond("", HttpStatusCode.NotFound, jsonHeaders)
            }
        )

        val userRepository = UserRepositoryImpl(
            client = httpClient,
        )
        assertFailsWith<NotFoundException> {
            userRepository.getUserInfo()
        }


    }

    @Test
    fun `should map user dto to user when getUserInfo is called`() = runTest {

        val httpClient = createHttpClient(
            userResponse = {
                respond(
                    content = Json.encodeToString(UserDto.serializer(), user),
                    headers = jsonHeaders,
                    status = HttpStatusCode.OK,
                )
            }
        )

        val userRepository = UserRepositoryImpl(
            client = httpClient,
        )
        userRepository.getUserInfo()
        verify { user.toDomain() }
    }


    private companion object {
        val user: UserDto = UserDto(
            firstName = "sam",
            lastName = "smith",
            imageUrl = "url",
            phoneNumber = "7777",
        )
    }
}