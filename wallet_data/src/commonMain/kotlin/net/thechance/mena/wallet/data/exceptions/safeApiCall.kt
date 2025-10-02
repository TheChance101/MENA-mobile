package net.thechance.mena.wallet.data.exceptions

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.exceptions.NoDataFoundException
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
    if (response.status.value == StatusCodes.NO_CONTENT) {
        throw NoDataFoundException("No Data Found: " + parseErrorMessage(response))
    }
    return when (response.status.value) {
        in StatusCodes.SUCCESS_START..StatusCodes.SUCCESS_END -> parseBody<T>(response)
        StatusCodes.UNAUTHORIZED -> throw UnknownException("Unauthorized: " + parseErrorMessage(response))
        StatusCodes.REQUEST_TIMEOUT -> throw UnknownException("Request timeout: " + parseErrorMessage(response))
        StatusCodes.TOO_MANY_REQUESTS -> throw UnknownException("Too many requests: " + parseErrorMessage(response))
        in StatusCodes.SERVER_ERROR_START..StatusCodes.SERVER_ERROR_END -> throw UnknownException("Server error: " + parseErrorMessage(response))
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

object StatusCodes {
    const val NO_CONTENT = 204
    const val UNAUTHORIZED = 401
    const val REQUEST_TIMEOUT = 408
    const val TOO_MANY_REQUESTS = 429
    const val SERVER_ERROR_START = 500
    const val SERVER_ERROR_END = 599
    const val SUCCESS_START = 200
    const val SUCCESS_END = 299
}
