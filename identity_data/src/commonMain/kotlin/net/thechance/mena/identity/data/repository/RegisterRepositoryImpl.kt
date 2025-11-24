package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import net.thechance.mena.identity.data.dto.auth.request.CheckUserExistenceRequestDto
import net.thechance.mena.identity.data.dto.auth.response.AuthenticationResponse
import net.thechance.mena.identity.data.dto.resetPassword.request.OtpRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.request.VerifyOtpRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.response.OtpResponse
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toDto
import net.thechance.mena.identity.data.utils.getJsonWithBody
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.UsernameAlreadyExistsException
import net.thechance.mena.identity.domain.model.RegisterRequest
import net.thechance.mena.identity.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val client: HttpClient
) : RegisterRepository {
    private var sessionId = ""

    override suspend fun requestOTP(phoneNumber: PhoneNumber, countryCodeName: String) {
        safeWrapper {
            val response: OtpResponse = client.postJson(
                OtpRequestDto(
                    phoneNumber.getFormattedPhoneNumber(), countryCodeName
                ), REGISTER_REQUEST_OTP
            )
            sessionId = response.sessionId
        }
    }

    override suspend fun verifyOTPCode(otpCode: String) {
        safeWrapper {
            client.postJson<VerifyOtpRequestDto, Unit>(
                VerifyOtpRequestDto(otpCode, sessionId), REGISTER_VERIFY_OTP
            )
        }
    }

    override suspend fun checkUserExistence(username: String): Boolean {
        return safeWrapper {
            runCatching {
                fetchUsernameExistence(username)
            }.fold(onSuccess = { exists ->
                exists.takeUnless { it }?.let { false } ?: throw UsernameAlreadyExistsException()
            }, onFailure = { throwable ->
                handleUsernameCheckExceptionOrRethrow(throwable)
            })
        }
    }

    private suspend fun fetchUsernameExistence(username: String): Boolean {
        return client.getJsonWithBody<CheckUserExistenceRequestDto, Boolean>(
            requestDto = CheckUserExistenceRequestDto(username = username),
            path = REGISTER_CHECK_USER_EXISTENCE
        )
    }

    private fun handleUsernameCheckExceptionOrRethrow(throwable: Throwable): Nothing {
        when (throwable) {
            is ClientRequestException -> handleUsernameCheckException(throwable)
            else -> throw throwable
        }
    }

    override suspend fun register(request: RegisterRequest): net.thechance.mena.identity.domain.model.AuthenticationTokens {
        return safeWrapper {
            val response: AuthenticationResponse = performRegisterRequest(request)
            response.toDomain()
        }
    }

    private suspend fun performRegisterRequest(request: RegisterRequest): AuthenticationResponse {
        return client.postJson(
            request.toDto(sessionId), REGISTER
        )
    }

    private fun handleUsernameCheckException(e: ClientRequestException): Nothing {
        when (e.response.status) {
            HttpStatusCode.Conflict -> throw UsernameAlreadyExistsException()
            else -> throw e
        }
    }

    companion object {
        const val REGISTER_REQUEST_OTP = "identity/authentication/register/request-otp"
        const val REGISTER_VERIFY_OTP = "identity/authentication/register/verify-otp"
        const val REGISTER_CHECK_USER_EXISTENCE = "identity/authentication/register/check-user-existence"
        const val REGISTER = "identity/authentication/register"
    }
}