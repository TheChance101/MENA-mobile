package net.thechance.mena.identity.data.mapper

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegistrationDraft
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RegistrationDraftMapperTest {

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with correct firstName`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals("John", result.firstName)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with correct lastName`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals("Doe", result.lastName)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with correct username`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals("testuser", result.username)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with correct password`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals("Password123", result.password)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with correct phoneNumber`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals("+964", result.phoneNumber?.countryCode)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with correct phoneNumber localNumber`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals("7901234567", result.phoneNumber?.localNumber)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with correct birthDate`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals(LocalDate(2000, 1, 1), result.birthDate)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with MALE gender`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertEquals(Gender.MALE, result.gender)
    }

    @Test
    fun `toDomain should map RegistrationDraftDto to RegistrationDraft with FEMALE gender`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "Jane",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "FEMALE"
        )

        val result = dto.toDomain()

        assertEquals(Gender.FEMALE, result.gender)
    }

    @Test
    fun `toDomain should return null phoneNumber when countryCode is null`() {
        val dto = RegistrationDraftDto(
            countryCode = null,
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertNull(result.phoneNumber)
    }

    @Test
    fun `toDomain should return null phoneNumber when localNumber is null`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = null,
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertNull(result.phoneNumber)
    }

    @Test
    fun `toDomain should return null birthDate when birthDateYear is null`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = null,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertNull(result.birthDate)
    }

    @Test
    fun `toDomain should return null birthDate when birthDateMonth is null`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = null,
            birthDateDay = 1,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertNull(result.birthDate)
    }

    @Test
    fun `toDomain should return null birthDate when birthDateDay is null`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = null,
            gender = "MALE"
        )

        val result = dto.toDomain()

        assertNull(result.birthDate)
    }

    @Test
    fun `toDomain should return null gender when gender is null`() {
        val dto = RegistrationDraftDto(
            countryCode = "+964",
            localNumber = "7901234567",
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDateYear = 2000,
            birthDateMonth = 1,
            birthDateDay = 1,
            gender = null
        )

        val result = dto.toDomain()

        assertNull(result.gender)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct firstName`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals("John", result.firstName)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct lastName`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals("Doe", result.lastName)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct username`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals("testuser", result.username)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct password`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals("Password123", result.password)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct countryCode from draft`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+20", "1234567890")

        val result = draft.toDto(phoneNumber)

        assertEquals("+964", result.countryCode)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct countryCode from fallback`() {
        val draft = RegistrationDraft(
            phoneNumber = null,
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+20", "1234567890")

        val result = draft.toDto(phoneNumber)

        assertEquals("+20", result.countryCode)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct localNumber from draft`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+20", "1234567890")

        val result = draft.toDto(phoneNumber)

        assertEquals("7901234567", result.localNumber)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct localNumber from fallback`() {
        val draft = RegistrationDraft(
            phoneNumber = null,
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+20", "1234567890")

        val result = draft.toDto(phoneNumber)

        assertEquals("1234567890", result.localNumber)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct birthDateYear`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals(2000, result.birthDateYear)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct birthDateMonth`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals(1, result.birthDateMonth)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct birthDateDay`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals(1, result.birthDateDay)
    }

    @Test
    fun `toDto should map RegistrationDraft to RegistrationDraftDto with correct gender name`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertEquals("MALE", result.gender)
    }

    @Test
    fun `toDto should return null birthDateYear when birthDate is null`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = null,
            gender = Gender.MALE
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertNull(result.birthDateYear)
    }

    @Test
    fun `toDto should return null gender when gender is null`() {
        val draft = RegistrationDraft(
            phoneNumber = PhoneNumber("+964", "7901234567"),
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123",
            birthDate = LocalDate(2000, 1, 1),
            gender = null
        )
        val phoneNumber = PhoneNumber("+964", "7901234567")

        val result = draft.toDto(phoneNumber)

        assertNull(result.gender)
    }
}