package net.thechance.mena.trends.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.dto.ProfileDto
import net.thechance.mena.trends.data.util.NetworkConstants.IDENTITY_PATH
import net.thechance.mena.trends.data.util.NetworkConstants.PROFILE_ENDPOINT
import net.thechance.mena.trends.domain.entity.Profile
import net.thechance.mena.trends.domain.repository.UserRepository
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserRepositoryImplTest {

    private lateinit var repository: UserRepository
    private lateinit var networkClient: NetworkClient

    @BeforeTest
    fun setup() {
        networkClient = mock<NetworkClient>()
        repository = UserRepositoryImpl(networkClient)
    }

    private fun mockHttpClient(response: ProfileDto): HttpClient {
        return HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            engine {
                addHandler { request ->
                    respond(
                        content = Json.encodeToString(ProfileDto.serializer(), response),
                        status = HttpStatusCode.OK,
                        headers = headersOf(
                            HttpHeaders.ContentType, ContentType.Application.Json.toString()
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `should return profile entity successfully when network client returns valid profile response`() = runTest {
        val expectedProfile = Profile(
            username = "nour",
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.png"
        )
        val profileDto = ProfileDto(
            username = "nour",
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.png"
        )

        val mockHttpClient = mockHttpClient(profileDto)
        everySuspend {
            networkClient.get("/$IDENTITY_PATH/$PROFILE_ENDPOINT", any<HttpRequestBuilder.() -> Unit>())
        } returns mockHttpClient.get {  "/$IDENTITY_PATH/$PROFILE_ENDPOINT"}

        val actualProfile = repository.getCurrentUserProfile()

        assertThat(actualProfile).isEqualTo(expectedProfile)
    }
}