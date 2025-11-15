package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dto.resetPassword.response.OtpResponse
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.data.utils.mockHttpClientError
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import kotlin.test.Test

class ResetPasswordRepositoryImplTest {
    private val client: HttpClient = mockk(relaxed = true)
    private var forgetPasswordRepository: ResetPasswordRepositoryImpl =
        ResetPasswordRepositoryImpl(client)

    @Test
    fun `requestOTP() should throw InvalidMobileNumberException when server returns 404`() =
        runTest {
            val client = mockHttpClientError(HttpStatusCode.NotFound)

            forgetPasswordRepository = ResetPasswordRepositoryImpl(client)

            assertFailure {
                forgetPasswordRepository.requestOTP(
                    phoneNumber = phoneNumber,
                    countryCodeName = countryCode
                )
            }.isInstanceOf<InvalidCredentialsException>()

        }

    @Test
    fun `requestOTP() should throw TooManyRequestsException when server returns 429`() = runTest {
        val client = mockHttpClientError(HttpStatusCode.TooManyRequests)

        forgetPasswordRepository = ResetPasswordRepositoryImpl(client)

        assertFailure {
            forgetPasswordRepository.requestOTP(
                phoneNumber = phoneNumber,
                countryCodeName = countryCode
            )
        }.isInstanceOf<TooManyRequestsException>()
    }

    @Test
    fun `verifyOTPCode() should throw InvalidOTPException when server returns 401`() {
        val client = mockHttpClientError(HttpStatusCode.Unauthorized)

        forgetPasswordRepository = ResetPasswordRepositoryImpl(client)

        runTest {
            assertFailure {
                forgetPasswordRepository.verifyOTPCode(
                    otpCode = "123456",
                )
            }.isInstanceOf<UnAuthorizedException>()
        }
    }

    @Test
    fun `verifyOTPCode() should throw OTPExpiredException when server returns 400`() {
        val client = mockHttpClientError(HttpStatusCode.BadRequest)

        forgetPasswordRepository = ResetPasswordRepositoryImpl(client)

        runTest {
            assertFailure {
                forgetPasswordRepository.verifyOTPCode(
                    otpCode = "123456",
                )
            }.isInstanceOf<InvalidRequestException>()
        }
    }

    @Test
    fun `requestOTP() should return session id when server returns 200`() = runTest {
        val client = mockHttpClient(OtpResponse("123"))

        forgetPasswordRepository = ResetPasswordRepositoryImpl(client)

        forgetPasswordRepository.requestOTP(phoneNumber, countryCode)
    }

    @Test
    fun `resetPassword() should not throw exception when server returns 200`(){
        val client = mockHttpClient(Unit)

        forgetPasswordRepository = ResetPasswordRepositoryImpl(client)

        runTest {
            forgetPasswordRepository.resetPassword(
                newPassword = "newPassword",
                confirmPassword = "newPassword",
            )
        }
    }

    private val phoneNumber = PhoneNumber("+964","07701231234")
    private val countryCode = "IQ"
}

