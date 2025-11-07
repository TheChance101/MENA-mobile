package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.data.dataSource.local.setting.imageUploadCompleted
import net.thechance.mena.identity.data.dataSource.local.setting.lastRegistrationCountryCode
import net.thechance.mena.identity.data.dataSource.local.setting.lastRegistrationLocalNumber
import net.thechance.mena.identity.data.dataSource.local.setting.lastRegistrationPhoneNumber
import net.thechance.mena.identity.data.mapper.RegistrationDraftDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toDto
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository

class RegistrationDraftRepositoryImpl(
    private val settings: Settings
) : RegistrationDraftRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun saveDraft(phoneNumber: PhoneNumber, draft: RegistrationDraft) {
        val key = getKey(phoneNumber)
        val dto = draft.toDto(phoneNumber)
        val jsonString = json.encodeToString(dto)
        settings.putString(key, jsonString)
        saveLastPhoneNumber(phoneNumber)
    }

    override suspend fun getDraft(phoneNumber: PhoneNumber): RegistrationDraft? {
        val key = getKey(phoneNumber)
        val jsonString = settings.getStringOrNull(key) ?: return null

        return try {
            val dto = json.decodeFromString<RegistrationDraftDto>(jsonString)
            dto.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getLastPhoneNumber(): PhoneNumber? {
        val countryCode = settings.lastRegistrationCountryCode
        val localNumber = settings.lastRegistrationLocalNumber

        return if (countryCode.isNotBlank() && localNumber.isNotBlank()) {
            PhoneNumber(countryCode, localNumber)
        } else {
            null
        }
    }

    override suspend fun clearDraft(phoneNumber: PhoneNumber) {
        val key = getKey(phoneNumber)
        settings.remove(key)
        // Note: We don't clear last phone number here because we need it
        // to navigate to upload screen when app reopens after registration
        // It will be cleared explicitly when registration is complete
    }

    override suspend fun clearLastPhoneNumber() {
        settings.lastRegistrationPhoneNumber = ""
        settings.lastRegistrationCountryCode = ""
        settings.lastRegistrationLocalNumber = ""
        settings.imageUploadCompleted = false
    }

    override suspend fun setImageUploadCompleted(completed: Boolean) {
        settings.imageUploadCompleted = completed
    }

    override suspend fun isImageUploadCompleted(): Boolean {
        return settings.imageUploadCompleted
    }

    private fun saveLastPhoneNumber(phoneNumber: PhoneNumber) {
        settings.lastRegistrationPhoneNumber = phoneNumber.getFormattedPhoneNumber()
        settings.lastRegistrationCountryCode = phoneNumber.countryCode
        settings.lastRegistrationLocalNumber = phoneNumber.localNumber
    }

    private fun getKey(phoneNumber: PhoneNumber): String {
        return "$REGISTRATION_DRAFT_PREFIX${phoneNumber.getFormattedPhoneNumber()}"
    }

    companion object {
        private const val REGISTRATION_DRAFT_PREFIX = "registration_draft_"
    }
}