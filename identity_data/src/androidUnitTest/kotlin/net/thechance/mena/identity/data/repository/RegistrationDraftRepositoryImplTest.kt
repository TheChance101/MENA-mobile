package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dataSource.local.setting.IMAGE_UPLOAD_COMPLETED
import net.thechance.mena.identity.data.dataSource.local.setting.LAST_REGISTRATION_COUNTRY_CODE
import net.thechance.mena.identity.data.dataSource.local.setting.LAST_REGISTRATION_LOCAL_NUMBER
import net.thechance.mena.identity.data.dataSource.local.setting.LAST_REGISTRATION_PHONE_NUMBER
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegistrationDraft
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegistrationDraftRepositoryImplTest {

    private val settings: Settings = mockk(relaxed = true)
    private val repository = RegistrationDraftRepositoryImpl(settings)

    @Test
    fun `saveDraft should save draft to settings with correct key`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")
        val draft = RegistrationDraft(
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123"
        )

        repository.saveDraft(phoneNumber, draft)

        verify { settings.putString("registration_draft_+9647901234567", any()) }
    }

    @Test
    fun `saveDraft should save last phone number to settings`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")
        val draft = RegistrationDraft(
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123"
        )

        repository.saveDraft(phoneNumber, draft)

        verify { settings.putString(LAST_REGISTRATION_PHONE_NUMBER, "+9647901234567") }
    }

    @Test
    fun `saveDraft should save last country code to settings`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")
        val draft = RegistrationDraft(
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123"
        )

        repository.saveDraft(phoneNumber, draft)

        verify { settings.putString(LAST_REGISTRATION_COUNTRY_CODE, "+964") }
    }

    @Test
    fun `saveDraft should save last local number to settings`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")
        val draft = RegistrationDraft(
            firstName = "John",
            lastName = "Doe",
            username = "testuser",
            password = "Password123"
        )

        repository.saveDraft(phoneNumber, draft)

        verify { settings.putString(LAST_REGISTRATION_LOCAL_NUMBER, "7901234567") }
    }

    @Test
    fun `getDraft should return null when no draft exists`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")
        every { settings.getStringOrNull("registration_draft_+9647901234567") } returns null

        val result = repository.getDraft(phoneNumber)

        assertNull(result)
    }

    @Test
    fun `getDraft should return RegistrationDraft when draft exists`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")
        val jsonString =
            """{"countryCode":"+964","localNumber":"7901234567","firstName":"John","lastName":"Doe","username":"testuser","password":"Password123","birthDateYear":2000,"birthDateMonth":1,"birthDateDay":1,"gender":"MALE"}"""
        every { settings.getStringOrNull("registration_draft_+9647901234567") } returns jsonString

        val result = repository.getDraft(phoneNumber)

        assertEquals("John", result?.firstName)
    }

    @Test
    fun `getDraft should return null when JSON parsing fails`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")
        every { settings.getStringOrNull("registration_draft_+9647901234567") } returns "invalid json"

        val result = repository.getDraft(phoneNumber)

        assertNull(result)
    }

    @Test
    fun `getLastPhoneNumber should return PhoneNumber when both country code and local number exist`() =
        runTest {
            every { settings.getString(LAST_REGISTRATION_COUNTRY_CODE, "") } returns "+964"
            every { settings.getString(LAST_REGISTRATION_LOCAL_NUMBER, "") } returns "7901234567"

            val result = repository.getLastPhoneNumber()

            assertEquals("+964", result?.countryCode)
        }

    @Test
    fun `getLastPhoneNumber should return PhoneNumber with correct local number`() = runTest {
        every { settings.getString(LAST_REGISTRATION_COUNTRY_CODE, "") } returns "+964"
        every { settings.getString(LAST_REGISTRATION_LOCAL_NUMBER, "") } returns "7901234567"

        val result = repository.getLastPhoneNumber()

        assertEquals("7901234567", result?.localNumber)
    }

    @Test
    fun `getLastPhoneNumber should return null when country code is blank`() = runTest {
        every { settings.getString(LAST_REGISTRATION_COUNTRY_CODE, "") } returns ""
        every { settings.getString(LAST_REGISTRATION_LOCAL_NUMBER, "") } returns "7901234567"

        val result = repository.getLastPhoneNumber()

        assertNull(result)
    }

    @Test
    fun `getLastPhoneNumber should return null when local number is blank`() = runTest {
        every { settings.getString(LAST_REGISTRATION_COUNTRY_CODE, "") } returns "+964"
        every { settings.getString(LAST_REGISTRATION_LOCAL_NUMBER, "") } returns ""

        val result = repository.getLastPhoneNumber()

        assertNull(result)
    }

    @Test
    fun `clearDraft should remove draft from settings`() = runTest {
        val phoneNumber = PhoneNumber("+964", "7901234567")

        repository.clearDraft(phoneNumber)

        verify { settings.remove("registration_draft_+9647901234567") }
    }

    @Test
    fun `clearLastPhoneNumber should clear last phone number from settings`() = runTest {
        repository.clearLastPhoneNumber()

        verify { settings.putString(LAST_REGISTRATION_PHONE_NUMBER, "") }
    }

    @Test
    fun `clearLastPhoneNumber should clear last country code from settings`() = runTest {
        repository.clearLastPhoneNumber()

        verify { settings.putString(LAST_REGISTRATION_COUNTRY_CODE, "") }
    }

    @Test
    fun `clearLastPhoneNumber should clear last local number from settings`() = runTest {
        repository.clearLastPhoneNumber()

        verify { settings.putString(LAST_REGISTRATION_LOCAL_NUMBER, "") }
    }

    @Test
    fun `clearLastPhoneNumber should set imageUploadCompleted to false`() = runTest {
        repository.clearLastPhoneNumber()

        verify { settings.putBoolean(IMAGE_UPLOAD_COMPLETED, false) }
    }

    @Test
    fun `setImageUploadCompleted should set imageUploadCompleted to true`() = runTest {
        repository.setImageUploadCompleted(true)

        verify { settings.putBoolean(IMAGE_UPLOAD_COMPLETED, true) }
    }

    @Test
    fun `setImageUploadCompleted should set imageUploadCompleted to false`() = runTest {
        repository.setImageUploadCompleted(false)

        verify { settings.putBoolean(IMAGE_UPLOAD_COMPLETED, false) }
    }

    @Test
    fun `isImageUploadCompleted should return true when image upload is completed`() = runTest {
        every { settings.getBoolean(IMAGE_UPLOAD_COMPLETED, false) } returns true

        val result = repository.isImageUploadCompleted()

        assertTrue(result)
    }

    @Test
    fun `isImageUploadCompleted should return false when image upload is not completed`() =
        runTest {
            every { settings.getBoolean(IMAGE_UPLOAD_COMPLETED, false) } returns false

            val result = repository.isImageUploadCompleted()

            assertFalse(result)
        }
}
