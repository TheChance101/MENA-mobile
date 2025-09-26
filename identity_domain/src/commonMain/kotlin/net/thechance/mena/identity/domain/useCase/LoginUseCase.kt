package net.thechance.mena.identity.domain.useCase

import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidPasswordException
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.MobileNumberValidator

open class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val mobileNumberValidator: MobileNumberValidator
) {
   open suspend fun login(countryCode: String, number: String, password: String) {
        if (!isPasswordValid(password)) throw InvalidPasswordException()
        if (!isMobileNumberValid(countryCode, number)) throw InvalidMobileNumberException(number)
        authenticationRepository.login(
            countryCode = countryCode,
            number = number,
            password = password
        )
    }

    open fun isMobileNumberValid(countryCode: String, number: String): Boolean {
        return mobileNumberValidator.isValid(countryCode = countryCode, number = number)
    }

    open fun isPasswordValid(password: String) = password.length >= PASSWORD_MIN_LENGTH

    private companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }
}