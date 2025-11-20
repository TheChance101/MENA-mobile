package net.thechance.mena.identity.domain.useCase.validation.mobileNumber

class PasswordValidator {
    fun isValid(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH &&
                password.any { it.isDigit() } &&
                password.any { it.isUpperCase() }
    }

    fun isPasswordMatch(password: String, confirmPassword: String) = password == confirmPassword

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}