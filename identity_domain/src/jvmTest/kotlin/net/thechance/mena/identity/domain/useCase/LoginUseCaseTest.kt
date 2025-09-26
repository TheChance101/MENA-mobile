package net.thechance.mena.identity.domain.useCase

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidPasswordException
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.MobileNumberValidator
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.ValidMobileNumbersDummyData
import org.junit.Test

internal class LoginUseCaseTest {
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val mobileNumberValidator = mockk<MobileNumberValidator>()
    private val loginUseCase = LoginUseCase(authenticationRepository, mobileNumberValidator)

    @Test
    fun `should return true when password length equal to 8`() {
        val password = "password"

        val isValid = loginUseCase.isPasswordValid(password)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return true when password length greater than 8`() {
        val password = "password1"

        val isValid = loginUseCase.isPasswordValid(password)

        assertThat(isValid).isTrue()
    }

    @Test
    fun `should return false when password is not valid`() {
        val password = "passwor"

        val isValid = loginUseCase.isPasswordValid(password)

        assertThat(isValid).isFalse()
    }

    @Test
    fun `should return true when mobile number is valid`() {
        every { mobileNumberValidator.isValid(any(), any()) } returns true

        val isMobileNumberValid = loginUseCase.isMobileNumberValid("+20", "01283393335")

        assertThat(isMobileNumberValid).isTrue()
    }

    @Test
    fun `should return true when mobile number is not valid`() {
        every { mobileNumberValidator.isValid(any(), any()) } returns false

        val isMobileNumberValid = loginUseCase.isMobileNumberValid("+200", "0183393335")

        assertThat(isMobileNumberValid).isFalse()
    }

    @Test
    fun `should login with valid credentials`() = runTest {
        every { mobileNumberValidator.isValid(any(), any()) } returns true
        coEvery { authenticationRepository.login(any(), any(), any()) } just Runs
        val password = "12345678"
        val countryCode = ValidMobileNumbersDummyData.MOROCCO.countryCode
        val number = ValidMobileNumbersDummyData.MOROCCO.mobileNumber

        val result = loginUseCase.login(countryCode, number, password)

        assertThat(result).isEqualTo(Unit)
    }

    @Test
    fun `should throw InvalidMobileNumberException when mobile number is not valid`() = runTest {
        every { mobileNumberValidator.isValid(any(), any()) } returns false
        val password = "12845678"
        val countryCode = ValidMobileNumbersDummyData.EGYPT.countryCode
        val number = ValidMobileNumbersDummyData.EGYPT.mobileNumber

        assertFailure {
            loginUseCase.login(countryCode, number, password)
        }.isInstanceOf(InvalidMobileNumberException::class)
    }

    @Test
    fun `should throw InvalidPasswordException when password is not valid`() = runTest {
        every { mobileNumberValidator.isValid(any(), any()) } returns true
        val password = "78"
        val countryCode = ValidMobileNumbersDummyData.IRAQ.countryCode
        val number = ValidMobileNumbersDummyData.IRAQ.mobileNumber

        assertFailure {
            loginUseCase.login(countryCode, number, password)
        }.isInstanceOf(InvalidPasswordException::class)
    }
}