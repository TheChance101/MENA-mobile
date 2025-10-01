package net.thechance.mena.wallet.data.exceptions

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.exceptions.NoTransactionsFoundException
import net.thechance.mena.wallet.domain.exceptions.UnknownException

suspend inline fun <reified T> safeApiCall(
    crossinline execute: suspend () -> HttpResponse
): T {
    val response = try {
        execute()
    } catch (e: IOException) {
        throw NoInternetException(e.message ?: "")
    } catch (e: Exception) {
        throw UnknownException(e.message ?: "")
    }
    if (response.status.value == 204) {
        throw NoTransactionsFoundException("No Transactions Found: " + parseErrorMessage(response))
    }
    return when (response.status.value) {
        in 200..299 -> parseBody<T>(response)
        401 -> throw UnknownException("Unauthorized: " + parseErrorMessage(response))
        408 -> throw UnknownException("Request timeout: " + parseErrorMessage(response))
        429 -> throw UnknownException("Too many requests: " + parseErrorMessage(response))
        in 500..599 -> throw UnknownException("Server error: " + parseErrorMessage(response))
        else -> throw UnknownException(parseErrorMessage(response))
    }
}

suspend inline fun <reified T> parseBody(response: HttpResponse): T {
    return try {
        response.body()
    } catch (e: SerializationException) {
        throw UnknownException("Error parsing response")
    } catch (e: Exception) {
        throw UnknownException("Unexpected error parsing response")
    }
}

suspend fun parseErrorMessage(response: HttpResponse): String {
    return try {
        response.body<ErrorDto>().message ?: "Unexpected error"
    } catch (e: Exception) {
        "Unexpected error"
    }
}
