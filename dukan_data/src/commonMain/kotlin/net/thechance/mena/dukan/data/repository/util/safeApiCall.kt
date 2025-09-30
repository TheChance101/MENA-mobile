package net.thechance.mena.dukan.data.repository.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException
import net.thechance.mena.dukan.domain.exceptions.DukanException
import net.thechance.mena.dukan.domain.exceptions.DukanNotFoundException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException

suspend inline fun <reified T> safeApiCall(
    crossinline block: suspend () -> HttpResponse
): T {
    val response = runCatching { block() }
        .getOrElse { e ->
            when (e) {
                is IOException -> throw NoInternetException(e.message.orEmpty())
                else -> throw DukanException(e.message.orEmpty())
            }
        }
    return handleResponse(response)
}

suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                response.body<T>()
            } catch (_: Exception) {
                throw DukanException("Error parsing response")
            }
        }
        400 -> throw DukanNotFoundException("Bad request")
        401 -> throw DukanException("Unauthorized")
        408 -> throw DukanException("Request timeout")
        429 -> throw DukanException("Too many requests")
        in 500..599 -> throw DukanException("Server error")
        else -> throw DukanException("Unexpected error")
    }
}