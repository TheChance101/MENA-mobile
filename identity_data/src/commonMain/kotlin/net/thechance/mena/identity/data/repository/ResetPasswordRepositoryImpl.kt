package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.dto.resetPassword.request.OtpRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.response.OtpResponse
import net.thechance.mena.identity.data.dto.resetPassword.request.ResetPasswordRequestDto
import net.thechance.mena.identity.data.dto.resetPassword.request.VerifyOtpRequestDto
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository

class ResetPasswordRepositoryImpl(
    private val client: HttpClient
) : ResetPasswordRepository {
    private var sessionId = ""

    override suspend fun requestOTP(phoneNumber: PhoneNumber, countryCodeName: String) {
        safeWrapper {
            val response: OtpResponse =
                client.postJson(
                    OtpRequestDto(
                        phoneNumber.getFormattedPhoneNumber(),
                        countryCodeName
                    ), RESET_PASSWORD_REQUEST_OTP
                )
            sessionId = response.sessionId
        }
    }

    override suspend fun verifyOTPCode(otpCode: String) {
        safeWrapper<String> {
            client.postJson(
                VerifyOtpRequestDto(
                    otpCode,
                    sessionId
                ), RESET_PASSWORD_VERIFY_OTP
            )
        }
    }

    override suspend fun resetPassword(newPassword: String, confirmPassword: String) {
        safeWrapper<String> {
            client.postJson(
                ResetPasswordRequestDto(newPassword, confirmPassword, sessionId),
                RESET_PASSWORD
            )
        }
    }

    companion object {
        const val RESET_PASSWORD_REQUEST_OTP = "identity/authentication/request-reset-password-otp"
        const val RESET_PASSWORD_VERIFY_OTP = "identity/authentication/verify-reset-password-otp"
        const val RESET_PASSWORD = "identity/authentication/reset-password"
    }
}