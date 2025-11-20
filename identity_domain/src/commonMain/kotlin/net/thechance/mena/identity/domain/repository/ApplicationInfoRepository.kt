package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.model.ContactInfo
import net.thechance.mena.identity.domain.model.PrivacyAndPolicy

interface ApplicationInfoRepository {
    suspend fun getPrivacyAndPolicy(): PrivacyAndPolicy
    suspend fun getContactInfo(): ContactInfo
}