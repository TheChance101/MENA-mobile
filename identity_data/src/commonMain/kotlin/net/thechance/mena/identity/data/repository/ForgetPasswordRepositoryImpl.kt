package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.dto.forgetPassword.OTPRequestDto
import net.thechance.mena.identity.data.dto.forgetPassword.OTPResponse
import net.thechance.mena.identity.data.dto.forgetPassword.VerifyOTPRequestDto
import net.thechance.mena.identity.data.dto.forgetPassword.VerifyOTPResponse
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.repository.ForgetPasswordRepository

class ForgetPasswordRepositoryImpl(
    private val client: HttpClient
) : ForgetPasswordRepository {
    private var sessionId = ""

    override suspend fun requestOTP(phoneNumber: String, countryCodeName: String) {
        safeWrapper {
            val response: OTPResponse =
                client.postJson(OTPRequestDto(phoneNumber, countryCodeName), REQUEST_OTP)
            sessionId = response.sessionId
        }
    }

    override suspend fun verifyOTPCode(otpCode: String) {
        safeWrapper<VerifyOTPResponse> {
            client.postJson(VerifyOTPRequestDto(otpCode, sessionId), VERIFY_OTP)
        }
    }

    companion object {
        const val REQUEST_OTP = "identity/otp/request"
        const val VERIFY_OTP = "identity/otp/verify"
    }
}