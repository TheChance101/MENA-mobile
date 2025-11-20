package net.thechance.mena.identity.domain.useCase.validation.password

import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordValidatorTest {

    private val validator = PasswordValidator()

    @Test
    fun `should return false when password is shorter than 8 characters`() {
        assertFalse(validator.isValid("Abc12"))
    }

    @Test
    fun `should return false when password has no digits`() {
        assertFalse(validator.isValid("Password"))
    }

    @Test
    fun `should return false when password has no uppercase letters`() {
        assertFalse(validator.isValid("password123"))
    }

    @Test
    fun `should return true when password has at least 8 characters, a digit, and an uppercase letter`() {
        assertTrue(validator.isValid("Password123"))
    }

    @Test
    fun `should return true for edge case exactly 8 characters with digit and uppercase`() {
        assertTrue(validator.isValid("Passw0rd"))
    }

    @Test
    fun `should return false when confirm password not match password`() {
        assertFalse(validator.isPasswordMatch("Password123", "DifferentPass123"))
    }

    @Test
    fun `should return true when confirm password matches password`() {
        assertTrue(validator.isPasswordMatch("Password123", "Password123"))
    }

}