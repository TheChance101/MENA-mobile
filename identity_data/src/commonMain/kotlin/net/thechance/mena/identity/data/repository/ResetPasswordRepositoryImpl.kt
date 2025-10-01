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
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.OtpExpiredException
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository

class ResetPasswordRepositoryImpl(
    private val client: HttpClient
) : ResetPasswordRepository {
    private var sessionId = ""

    override suspend fun requestOTP(phoneNumber: PhoneNumber, countryCodeName: String) {
        forgetPasswordSafeWrapper {
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
        forgetPasswordSafeWrapper<String> {
            client.postJson(
                VerifyOtpRequestDto(
                    otpCode,
                    sessionId
                ), VERIFY_OTP
            )
        }
    }

    override suspend fun resetPassword(
        newPassword: String,
        confirmPassword: String,
        phoneNumber: PhoneNumber
    ) {
        forgetPasswordSafeWrapper<String> {
            client.postJson(
                ResetPasswordRequestDto(newPassword, confirmPassword, phoneNumber.getFormattedPhoneNumber()),
                RESET_PASSWORD
            )
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
                    HttpStatusCode.BadRequest -> throw OtpExpiredException()
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