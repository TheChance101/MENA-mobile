package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import net.thechance.mena.identity.data.dto.resetPassword.OTPRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.OTPResponse
import net.thechance.mena.identity.data.dto.resetPassword.ResetPasswordRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.VerifyOTPRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.VerifyOTPResponse
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.OTPExpiredException
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository

class ResetPasswordRepositoryImpl(
    private val client: HttpClient
) : ResetPasswordRepository {
    private var sessionId = ""

    override suspend fun requestOTP(phoneNumber: String, countryCodeName: String) {
        forgetPasswordSafeWrapper {
            val response: OTPResponse =
                client.postJson(OTPRequestDto(phoneNumber, countryCodeName), REQUEST_OTP)
            sessionId = response.sessionId
        }
    }

    override suspend fun verifyOTPCode(otpCode: String , phoneNumber: String) {
        forgetPasswordSafeWrapper<VerifyOTPResponse> {
            client.postJson(VerifyOTPRequestDto(otpCode,phoneNumber ,sessionId), VERIFY_OTP)
        }
    }

    override suspend fun resetPassword(
        newPassword: String,
        confirmPassword: String,
        phoneNumber: String
    ) {
        forgetPasswordSafeWrapper<String> {
            client.postJson(ResetPasswordRequestDto(newPassword, confirmPassword, phoneNumber), RESET_PASSWORD)
        }
    }

    private suspend fun <T> forgetPasswordSafeWrapper(block: suspend () -> T): T {
        return safeWrapper {
            try {
                return@safeWrapper block()
            } catch (e: ClientRequestException) {
                when (e.response.status) {
                    HttpStatusCode.Unauthorized -> throw InvalidOTPException()
                    HttpStatusCode.NotFound -> throw InvalidMobileNumberException("")
                    HttpStatusCode.BadRequest -> throw OTPExpiredException()
                    else -> throw e
                }
            }
        }
    }

    private companion object {
        const val REQUEST_OTP = "identity/request-reset-password-otp"
        const val VERIFY_OTP = "identity/verify-otp"
        const val RESET_PASSWORD = "identity/reset-password"
    }
}