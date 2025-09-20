package net.thechance.mena.faith.data.utils

import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.faith.domain.exception.NetworkException
import net.thechance.mena.faith.domain.exception.NoInternetException
import net.thechance.mena.faith.domain.exception.UnauthorizedException
import net.thechance.mena.faith.domain.exception.UnknownException


suspend inline fun <reified T> handleRequest(
    apiCall: suspend () -> HttpResponse,
): T {
    val response = try {
        apiCall()
    } catch (e: Exception) {
        Napier.d(tag = "NetworkError", message = e.message.toString())
        throw mapToNetworkException(e)
    }

    return when (response.status) {
        HttpStatusCode.OK -> {
            try {
                response.body<T>()
            } catch (e: Exception) {
                Napier.d(tag = "NetworkError", message = "Error parsing response: ${e.message}")
                throw NetworkException()
            }
        }

        HttpStatusCode.Unauthorized -> {
            Napier.d(tag = "NetworkError", message = "Unauthorized access")
            throw UnauthorizedException()
        }

        HttpStatusCode.TooManyRequests -> {
            Napier.d(tag = "NetworkError", message = "Too many requests")
            throw NetworkException()
        }

        HttpStatusCode.RequestTimeout -> {
            Napier.d(tag = "NetworkError", message = "Request timed out")
            throw NetworkException()
        }

        500..599 -> {
            Napier.d(tag = "NetworkError", message = "Server error: ${response.status.value}")
            throw NetworkException()
        }

        else -> {
            Napier.d(tag = "NetworkError", message = "Unknown error: ${response.status.value}")
            throw UnknownException()
        }
    }
}

fun mapToNetworkException(e: Throwable): NetworkException {
    return when (e) {
        is IOException -> NoInternetException()
        is SerializationException -> NetworkException()
        else -> UnknownException()
    } as NetworkException
}
