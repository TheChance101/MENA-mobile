package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.RegistrationDraft

interface RegistrationDraftRepository {
    suspend fun saveDraft(phoneNumber: PhoneNumber, draft: RegistrationDraft)
    suspend fun getDraft(phoneNumber: PhoneNumber): RegistrationDraft?
    suspend fun getLastPhoneNumber(): PhoneNumber?
    suspend fun clearDraft(phoneNumber: PhoneNumber)
    suspend fun clearLastPhoneNumber()
    suspend fun setImageUploadCompleted(completed: Boolean)
    suspend fun isImageUploadCompleted(): Boolean
}

