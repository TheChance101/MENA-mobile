package net.thechance.mena.identity.data.utils

import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.exception.UnknownException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import org.junit.Test
import assertk.assertFailure
import assertk.assertions.isInstanceOf

class SafeWrapperTest {

    @Test
    fun `safeWrapper should return result when block succeeds`() = runTest {
        val result = safeWrapper {
            "success"
        }

        assert(result == "success")
    }

    @Test
    fun `safeWrapper should throw UnAuthorizedException when ClientRequestException with 401`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.Unauthorized)

        assertFailure {
            safeWrapper {
                client.postJson<Unit, Unit>(Unit, "test")
            }
        }.isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `safeWrapper should throw InvalidCredentialsException when ClientRequestException with 404`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.NotFound)

        assertFailure {
            safeWrapper {
                client.postJson<Unit, Unit>(Unit, "test")
            }
        }.isInstanceOf<InvalidCredentialsException>()
    }

    @Test
    fun `safeWrapper should throw UserIsBlockedException when ClientRequestException with 403`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.Forbidden)

        assertFailure {
            safeWrapper {
                client.postJson<Unit, Unit>(Unit, "test")
            }
        }.isInstanceOf<UserIsBlockedException>()
    }

    @Test
    fun `safeWrapper should throw TooManyRequestsException when ClientRequestException with 429`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.TooManyRequests)

        assertFailure {
            safeWrapper {
                client.postJson<Unit, Unit>(Unit, "test")
            }
        }.isInstanceOf<TooManyRequestsException>()
    }

    @Test
    fun `safeWrapper should throw InvalidRequestException when ClientRequestException with 400`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.BadRequest)

        assertFailure {
            safeWrapper {
                client.postJson<Unit, Unit>(Unit, "test")
            }
        }.isInstanceOf<InvalidRequestException>()
    }

    @Test
    fun `safeWrapper should throw UnknownException when ClientRequestException with other status`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.InternalServerError)

        assertFailure {
            safeWrapper {
                client.postJson<Unit, Unit>(Unit, "test")
            }
        }.isInstanceOf<UnknownException>()
    }

    @Test
    fun `safeWrapper should throw NoNetworkException when UnresolvedAddressException occurs`() = runTest {
        val exception = UnresolvedAddressException()

        assertFailure {
            safeWrapper {
                throw exception
            }
        }.isInstanceOf<NoNetworkException>()
    }

    @Test
    fun `safeWrapper should rethrow other exceptions`() = runTest {
        val exception = IllegalArgumentException("Test exception")

        assertFailure {
            safeWrapper {
                throw exception
            }
        }.isInstanceOf<IllegalArgumentException>()
    }
}

