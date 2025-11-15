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
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.exception.PhoneNumberAlreadyExistsException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import kotlin.test.Test

class RegisterRepositoryImplTest {
    private val client: HttpClient = mockk(relaxed = true)
    private var registerRepository: RegisterRepositoryImpl =
        RegisterRepositoryImpl(client)

    @Test
    fun `requestOTP() should return session id when server returns 200`() = runTest {
        val client = mockHttpClient(OtpResponse("session123"))

        registerRepository = RegisterRepositoryImpl(client)

        registerRepository.requestOTP(phoneNumber, countryCode)
    }

    @Test
    fun `requestOTP() should throw PhoneNumberAlreadyExistsException when server returns 409`() =
        runTest {
            val client = mockHttpClientError(HttpStatusCode.Conflict)

            registerRepository = RegisterRepositoryImpl(client)

            assertFailure {
                registerRepository.requestOTP(
                    phoneNumber = phoneNumber,
                    countryCodeName = countryCode
                )
            }.isInstanceOf<PhoneNumberAlreadyExistsException>()
        }

    @Test
    fun `verifyOTPCode() should not throw exception when server returns 200`() = runTest {
        val client = mockHttpClient(Unit)
        val requestOtpClient = mockHttpClient(OtpResponse("session123"))

        registerRepository = RegisterRepositoryImpl(requestOtpClient)
        registerRepository.requestOTP(phoneNumber, countryCode)

        registerRepository = RegisterRepositoryImpl(client)
        registerRepository.verifyOTPCode("123456")
    }

    @Test
    fun `verifyOTPCode() should throw InvalidOTPException when server returns 401`() = runTest {
        val requestOtpClient = mockHttpClient(OtpResponse("session123"))
        val client = mockHttpClientError(HttpStatusCode.Unauthorized)

        registerRepository = RegisterRepositoryImpl(requestOtpClient)
        registerRepository.requestOTP(phoneNumber, countryCode)

        registerRepository = RegisterRepositoryImpl(client)

        assertFailure {
            registerRepository.verifyOTPCode(otpCode = "123456")
        }.isInstanceOf<UnAuthorizedException>()
    }

    @Test
    fun `verifyOTPCode() should throw OtpExpiredException when server returns 400`() = runTest {
        val requestOtpClient = mockHttpClient(OtpResponse("session123"))
        val client = mockHttpClientError(HttpStatusCode.BadRequest)

        registerRepository = RegisterRepositoryImpl(requestOtpClient)
        registerRepository.requestOTP(phoneNumber, countryCode)

        registerRepository = RegisterRepositoryImpl(client)

        assertFailure {
            registerRepository.verifyOTPCode(otpCode = "123456")
        }.isInstanceOf<InvalidRequestException>()
    }

    @Test
    fun `verifyOTPCode() should throw PhoneNumberAlreadyExistsException when server returns 409`() =
        runTest {
            val requestOtpClient = mockHttpClient(OtpResponse("session123"))
            val client = mockHttpClientError(HttpStatusCode.Conflict)

            registerRepository = RegisterRepositoryImpl(requestOtpClient)
            registerRepository.requestOTP(phoneNumber, countryCode)

            registerRepository = RegisterRepositoryImpl(client)

            assertFailure {
                registerRepository.verifyOTPCode(otpCode = "123456")
            }.isInstanceOf<PhoneNumberAlreadyExistsException>()
        }

    private val phoneNumber = PhoneNumber("+964", "07701231234")
    private val countryCode = "IQ"
}