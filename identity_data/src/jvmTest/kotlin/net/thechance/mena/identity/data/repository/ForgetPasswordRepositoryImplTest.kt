package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.utils.mockHttpClientError
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.OTPExpiredException
import kotlin.test.Test

class ForgetPasswordRepositoryImplTest {
    private val client: HttpClient = mockk(relaxed = true)
    private var forgetPasswordRepository: ForgetPasswordRepositoryImpl =
        ForgetPasswordRepositoryImpl(client)

    @Test
    fun `requestOTP() should throw InvalidMobileNumberException when server returns 404`() =
        runTest {
            val client = mockHttpClientError(HttpStatusCode.NotFound)

            forgetPasswordRepository = ForgetPasswordRepositoryImpl(client)

            assertFailure {
                forgetPasswordRepository.requestOTP(
                    phoneNumber = phoneNumber,
                    countryCodeName = countryCode
                )
            }.isInstanceOf<InvalidMobileNumberException>()

        }

    @Test
    fun `verifyOTPCode() should throw InvalidOTPException when server returns 401`() {
        val client = mockHttpClientError(HttpStatusCode.Unauthorized)

        forgetPasswordRepository = ForgetPasswordRepositoryImpl(client)

        runTest {
            assertFailure {
                forgetPasswordRepository.verifyOTPCode(
                    otpCode = "123456",
                    phoneNumber = phoneNumber
                )
            }.isInstanceOf<InvalidOTPException>()
        }
    }

    @Test
    fun `verifyOTPCode() should throw OTPExpiredException when server returns 400`() {
        val client = mockHttpClientError(HttpStatusCode.BadRequest)

        forgetPasswordRepository = ForgetPasswordRepositoryImpl(client)

        runTest {
            assertFailure {
                forgetPasswordRepository.verifyOTPCode(
                    otpCode = "123456",
                    phoneNumber = phoneNumber
                )
            }.isInstanceOf<OTPExpiredException>()
        }
    }

    private val phoneNumber = "07701231234"
    private val countryCode = "IQ"
}

