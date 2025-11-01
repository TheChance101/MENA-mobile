package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import net.thechance.mena.identity.data.dto.resetPassword.OtpRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.OtpResponse
import net.thechance.mena.identity.data.dto.resetPassword.ResetPasswordRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.VerifyOtpRequestDto
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.OtpExpiredException
import net.thechance.mena.identity.domain.exception.PhoneNumberAlreadyExistsException
import net.thechance.mena.identity.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val client: HttpClient
) : RegisterRepository {
    private var sessionId = ""

    override suspend fun requestOTP(phoneNumber: PhoneNumber, countryCodeName: String) {
        registerSafeWrapper {
            val response: OtpResponse =
                client.postJson(
                    OtpRequestDto(
                        phoneNumber.getFormattedPhoneNumber(),
                        countryCodeName
                    ), REQUEST_OTP
                )
            sessionId = response.sessionId
        }
    }

    override suspend fun verifyOTPCode(otpCode: String) {
        registerSafeWrapper<String> {
            client.postJson(
                VerifyOtpRequestDto(
                    otpCode,
                    sessionId
                ), VERIFY_OTP
            )
        }
    }

    override suspend fun createPassword(newPassword: String, confirmPassword: String) {
        registerSafeWrapper<String> {
            client.postJson(
                ResetPasswordRequestDto(newPassword, confirmPassword, sessionId),
                CREATE_PASSWORD
            )
        }
    }

    private suspend fun <T> registerSafeWrapper(block: suspend () -> T): T {
        return safeWrapper {
            try {
                return@safeWrapper block()
            } catch (e: ClientRequestException) {
                when (e.response.status) {
                    HttpStatusCode.Unauthorized -> throw InvalidOTPException()
                    HttpStatusCode.Conflict -> throw PhoneNumberAlreadyExistsException()
                    HttpStatusCode.BadRequest -> throw OtpExpiredException()
                    else -> throw e
                }
            }
        }
    }

    companion object {
        const val REQUEST_OTP = "identity/authentication/request-register-otp"
        const val VERIFY_OTP = "identity/authentication/verify-otp"
        const val CREATE_PASSWORD = "identity/authentication/create-password"
    }
}