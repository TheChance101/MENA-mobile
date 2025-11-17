package net.thechance.mena.dukan.data.util.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.CancellationException
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
import net.thechance.mena.identity.domain.exception.NoNetworkException as IdentityNoInternetConnection


internal suspend inline fun <reified T> safeApiCall(
    crossinline block: suspend () -> HttpResponse
): T {
    val response = runCatching { block() }
        .getOrElse { e ->
            logger.error("Network error: ${e.message}")
            when (e) {
                is CancellationException -> throw e
                is IdentityNoInternetConnection -> throw NoInternetException(e.message.orEmpty())
                is UnresolvedAddressException -> throw NoInternetException(e.message.orEmpty())
                else -> throw DukanException(e.message.orEmpty())
            }
        }
    return handleResponse(response)
}


internal suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                response.body<T>()
            } catch (e: Exception) {
                logger.error("Error parsing response ${e.message}")
                throw DukanException("Error parsing response")
            }
        }

        400, 404, 409 -> {
            logger.error("Error response: ${response.status.description}")
            throw mapErrorResponseToException(parseErrorResponse(response))
        }

        401 -> throw UnAuthorizedException("Unauthorized")

        else -> {
            logger.error("Unexpected error: ${response.status.description}")
            throw DukanException("Unexpected error")
        }
    }
}

internal suspend fun parseErrorResponse(response: HttpResponse): ErrorResponse {
    return try {
        response.body<ErrorResponse>()
    } catch (e: Exception) {
        logger.error("Error parsing error response ${e.message}")
        ErrorResponse(e.message.orEmpty())
    }
}

internal fun mapErrorResponseToException(errorResponse: ErrorResponse): Exception {
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
        DukanErrorCodes.SHELF_NAME_NOT_CHANGED -> DuplicateNameException("Shelf name not changed: ${errorResponse.message}")
        DukanErrorCodes.CART_NOT_FOUND -> NoSuchItemException("Cart not found: ${errorResponse.message}")
        DukanErrorCodes.PRODUCT_NOT_IN_CART -> NoSuchItemException("Product not in cart: ${errorResponse.message}")
        DukanErrorCodes.PRODUCT_ALREADY_IN_CART -> NoSuchItemException("Product already in cart: ${errorResponse.message}")
        DukanErrorCodes.FORBIDDEN_ORDER_ACCESS -> UnAuthorizedException("Forbidden order access: ${errorResponse.message}")
        DukanErrorCodes.ORDER_NOT_FOUND -> NoSuchItemException("Order not found: ${errorResponse.message}")
        else -> DukanException(errorResponse.message)
    }
}

private val logger = KtorSimpleLogger("HttpResponseHandler")
