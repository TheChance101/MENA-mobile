package net.thechance.mena.faith.data.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.faith.domain.exception.FaithException

suspend inline fun <reified T> safeApiCall(
    apiCall: suspend () -> HttpResponse,
): T {
    val response = try {
        apiCall()
    } catch (e: Exception) {
        println("Network error: ${e.message}")
        throw mapToNetworkException(e)
    }

    return when (response.status) {
        HttpStatusCode.OK -> {
            try {
                response.body<T>()
            } catch (e: Exception) {
                println("Error parsing response: ${e.message}")
                throw FaithException.NetworkException
            }
        }

        HttpStatusCode.Unauthorized -> {
            println("Unauthorized access")
            throw FaithException.UnauthorizedException
        }

        HttpStatusCode.TooManyRequests -> {
            println("Too many requests")
            throw FaithException.NetworkException
        }

        HttpStatusCode.RequestTimeout -> {
            println("Request timed out")
            throw FaithException.NetworkException
        }

        else -> {
            if (response.status.value in 500..599) {
                println("Server error: ${response.status.value}")
                throw FaithException.NetworkException
            } else {
                println("Unknown error: ${response.status.value}")
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