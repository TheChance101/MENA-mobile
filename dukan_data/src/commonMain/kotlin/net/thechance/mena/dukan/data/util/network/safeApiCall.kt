package net.thechance.mena.dukan.data.util.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException
import net.thechance.mena.dukan.data.dto.ErrorResponse
import net.thechance.mena.dukan.data.util.constants.DukanErrorCodes
import net.thechance.mena.dukan.domain.exceptions.CreationFailedException
import net.thechance.mena.dukan.domain.exceptions.DeletionNotAllowedException
import net.thechance.mena.dukan.domain.exceptions.DukanException
import net.thechance.mena.dukan.domain.exceptions.DuplicateNameException
import net.thechance.mena.dukan.domain.exceptions.InvalidImageFormatException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.exceptions.UnAuthorizedException
import net.thechance.mena.dukan.domain.exceptions.UploadingFailedException

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

        401 -> throw UnAuthorizedException("Unauthorized")
        404 -> {
            val errorResponse = try {
                response.body<ErrorResponse>()
            } catch (_: Exception) {
                ErrorResponse("Not found")
            }
            throw mapErrorResponseToException(errorResponse)
        }

        409 -> {
            val errorResponse = try {
                response.body<ErrorResponse>()
            } catch (_: Exception) {
                ErrorResponse("Conflict")
            }
            throw mapErrorResponseToException(errorResponse)
        }

        else -> throw DukanException("Unexpected error")
    }
}

fun mapErrorResponseToException(errorResponse: ErrorResponse): Exception {
    val errorCode = errorResponse.errorCode
    return when (errorCode) {
        DukanErrorCodes.DUKAN_CREATION_FAILED -> CreationFailedException("Dukan creation failed: ${errorResponse.message}")
        DukanErrorCodes.DUKAN_NOT_FOUND -> NoSuchItemException("Dukan not found: ${errorResponse.message}")
        DukanErrorCodes.INVALID_IMAGE_FORMAT -> InvalidImageFormatException("Invalid image format: ${errorResponse.message}")
        DukanErrorCodes.IMAGE_UPLOAD_FAILED -> UploadingFailedException("Image upload failed: ${errorResponse.message}")
        DukanErrorCodes.PRODUCT_NOT_FOUND -> NoSuchItemException("Product not found: ${errorResponse.message}")
        DukanErrorCodes.DUKAN_PRODUCT_CREATION_FAILED -> CreationFailedException("Product creation failed: ${errorResponse.message}")
        DukanErrorCodes.PRODUCT_NAME_ALREADY_TAKEN -> DuplicateNameException("Product name already taken: ${errorResponse.message}")
        DukanErrorCodes.SHELF_DELETION_NOT_ALLOWED -> DeletionNotAllowedException("Shelf deletion not allowed: ${errorResponse.message}")
        DukanErrorCodes.SHELF_NOT_FOUND -> NoSuchItemException("Shelf not found: ${errorResponse.message}")
        DukanErrorCodes.SHELF_NAME_ALREADY_TAKEN -> DuplicateNameException("Shelf name already taken: ${errorResponse.message}")
        else -> DukanException(errorResponse.message)
    }
}
