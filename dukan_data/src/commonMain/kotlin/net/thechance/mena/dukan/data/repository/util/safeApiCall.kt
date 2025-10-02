package net.thechance.mena.dukan.data.repository.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException
import net.thechance.mena.dukan.data.repository.dto.DukanErrorCodes
import net.thechance.mena.dukan.data.repository.dto.ErrorResponse
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

        400 -> {
            val errorResponse = try {
                response.body<ErrorResponse>()
            } catch (_: Exception) {
                ErrorResponse("Bad request")
            }
            throw mapErrorResponseToException(errorResponse)
        }

        401 -> throw DukanException("Unauthorized")
        404 -> {
            val errorResponse = try {
                response.body<ErrorResponse>()
            } catch (_: Exception) {
                ErrorResponse("Not found")
            }
            throw mapErrorResponseToException(errorResponse)
        }

        408 -> throw DukanException("Request timeout")
        429 -> throw DukanException("Too many requests")
        in 500..599 -> throw DukanException("Server error")
        else -> throw DukanException("Unexpected error")
    }
}

fun mapErrorResponseToException(errorResponse: ErrorResponse): Exception {
    val errorCode = errorResponse.errorCode
    return when (errorCode) {
        DukanErrorCodes.DUKAN_CREATION_FAILED -> DukanException("Dukan creation failed: ${errorResponse.message}")
        DukanErrorCodes.DUKAN_NOT_FOUND -> DukanNotFoundException("Dukan not found: ${errorResponse.message}")
        DukanErrorCodes.INVALID_IMAGE_FORMAT -> DukanException("Invalid image format: ${errorResponse.message}")
        DukanErrorCodes.IMAGE_UPLOAD_FAILED -> DukanException("Image upload failed: ${errorResponse.message}")
        DukanErrorCodes.PRODUCT_NOT_FOUND -> DukanNotFoundException("Product not found: ${errorResponse.message}")
        DukanErrorCodes.DUKAN_PRODUCT_CREATION_FAILED -> DukanException("Product creation failed: ${errorResponse.message}")
        DukanErrorCodes.PRODUCT_NAME_ALREADY_TAKEN -> DukanException("Product name already taken: ${errorResponse.message}")
        DukanErrorCodes.SHELF_DELETION_NOT_ALLOWED -> DukanException("Shelf deletion not allowed: ${errorResponse.message}")
        DukanErrorCodes.SHELF_NOT_FOUND -> DukanNotFoundException("Shelf not found: ${errorResponse.message}")
        DukanErrorCodes.SHELF_NAME_ALREADY_TAKEN -> DukanException("Shelf name already taken: ${errorResponse.message}")
        else -> DukanException(errorResponse.message)
    }
}
