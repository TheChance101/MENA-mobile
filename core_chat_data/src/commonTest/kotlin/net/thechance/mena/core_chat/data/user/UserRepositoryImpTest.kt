package net.thechance.mena.core_chat.data.user

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.contacts.createHttpClient
import net.thechance.mena.core_chat.data.contacts.defaultUserInfoResponse
import net.thechance.mena.core_chat.data.contacts.jsonHeaders
import net.thechance.mena.core_chat.data.contacts.jsonSerialization
import net.thechance.mena.core_chat.data.repository.UserRepositoryImpl
import net.thechance.mena.core_chat.data.source.remote.dto.UserDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserRepositoryImpTest {

    @Test
    fun `should get user info from remote source when getUserInfo is called`() = runTest {

        var httpClient = createHttpClient(
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

        var userRepository = UserRepositoryImpl(
            client = httpClient,
        )

        val result = userRepository.getUserInfo()
        assertThat(result).isEqualTo(user.toDomain())
    }



    private companion object{
        val user: UserDto = UserDto(
            firstName = "sam",
            lastName = "smith",
            imageUrl = "url",
            phoneNumber = "7777",
        )
    }
}