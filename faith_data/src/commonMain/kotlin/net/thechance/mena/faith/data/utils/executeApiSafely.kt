package net.thechance.mena.faith.data.utils

import de.jensklingenberg.ktorfit.Response
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.faith.domain.exception.FaithException

suspend inline fun <reified T> executeApiSafely(
    apiCall: suspend () -> Response<T>,
): T {
    val response = runCatching { apiCall() }
        .onFailure { e ->
            Napier.d("Network error: ${e.message}")
            throw mapToNetworkException(e)
        }
        .getOrThrow()

    return when (response.status) {
        HttpStatusCode.OK -> {
            runCatching {
                response.body() ?: run {
                    Napier.d("Response body is null")
                    throw FaithException.NetworkException
                }
            }
                .onFailure { e ->
                    Napier.d("Error parsing response: ${e.message}")
                    throw FaithException.NetworkException
                }
                .getOrThrow()
        }

        HttpStatusCode.Unauthorized -> {
            Napier.d("Unauthorized access")
            throw FaithException.UnauthorizedException
        }

        HttpStatusCode.TooManyRequests -> {
            Napier.d("Too many requests")
            throw FaithException.NetworkException
        }

        HttpStatusCode.RequestTimeout -> {
            Napier.d("Request timed out")
            throw FaithException.NetworkException
        }

        else -> {
            if (response.status.value in 500..599) {
                Napier.d("Server error: ${response.status.value}")
                throw FaithException.NetworkException
            } else {
                Napier.d("Unknown error: ${response.status.value}")
                throw FaithException.UnknownException
            }
        }
    }
}

fun mapToNetworkException(e: Throwable): FaithException {
    return when (e) {
        is IOException -> FaithException.NoInternetException
        is SerializationException -> FaithException.NetworkException
        else -> FaithException.UnknownException
    }
}