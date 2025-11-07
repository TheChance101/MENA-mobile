package net.thechance.mena.identity.data.repository

import assertk.assertFailure
import assertk.assertions.isInstanceOf
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dto.resetPassword.response.OtpResponse
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.data.utils.mockHttpClientError
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.OtpExpiredException
import net.thechance.mena.identity.domain.exception.PhoneNumberAlreadyExistsException
import kotlin.test.Test

class RegisterRepositoryImplTest {
    private val client: HttpClient = mockk(relaxed = true)
    private val settings: Settings = mockk(relaxed = true)
    private var registerRepository: RegisterRepositoryImpl =
        RegisterRepositoryImpl(client, settings)

    @Test
    fun `requestOTP() should return session id when server returns 200`() = runTest {
        val client = mockHttpClient(OtpResponse("session123"))

        registerRepository = RegisterRepositoryImpl(client, settings)

        registerRepository.requestOTP(phoneNumber, countryCode)
    }

    @Test
    fun `requestOTP() should throw PhoneNumberAlreadyExistsException when server returns 409`() =
        runTest {
            val client = mockHttpClientError(HttpStatusCode.Conflict)

            registerRepository = RegisterRepositoryImpl(client, settings)

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

        registerRepository = RegisterRepositoryImpl(requestOtpClient, settings)
        registerRepository.requestOTP(phoneNumber, countryCode)

        registerRepository = RegisterRepositoryImpl(client, settings)
        registerRepository.verifyOTPCode("123456")
    }

    @Test
    fun `verifyOTPCode() should throw InvalidOTPException when server returns 401`() = runTest {
        val requestOtpClient = mockHttpClient(OtpResponse("session123"))
        val client = mockHttpClientError(HttpStatusCode.Unauthorized)

        registerRepository = RegisterRepositoryImpl(requestOtpClient, settings)
        registerRepository.requestOTP(phoneNumber, countryCode)

        registerRepository = RegisterRepositoryImpl(client, settings)

        assertFailure {
            registerRepository.verifyOTPCode(otpCode = "123456")
        }.isInstanceOf<InvalidOTPException>()
    }

    @Test
    fun `verifyOTPCode() should throw OtpExpiredException when server returns 400`() = runTest {
        val requestOtpClient = mockHttpClient(OtpResponse("session123"))
        val client = mockHttpClientError(HttpStatusCode.BadRequest)

        registerRepository = RegisterRepositoryImpl(requestOtpClient, settings)
        registerRepository.requestOTP(phoneNumber, countryCode)

        registerRepository = RegisterRepositoryImpl(client, settings)

        assertFailure {
            registerRepository.verifyOTPCode(otpCode = "123456")
        }.isInstanceOf<OtpExpiredException>()
    }

    @Test
    fun `verifyOTPCode() should throw PhoneNumberAlreadyExistsException when server returns 409`() =
        runTest {
            val requestOtpClient = mockHttpClient(OtpResponse("session123"))
            val client = mockHttpClientError(HttpStatusCode.Conflict)

            registerRepository = RegisterRepositoryImpl(requestOtpClient, settings)
            registerRepository.requestOTP(phoneNumber, countryCode)

            registerRepository = RegisterRepositoryImpl(client, settings)

            assertFailure {
                registerRepository.verifyOTPCode(otpCode = "123456")
            }.isInstanceOf<PhoneNumberAlreadyExistsException>()
        }

    private val phoneNumber = PhoneNumber("+964", "07701231234")
    private val countryCode = "IQ"
}