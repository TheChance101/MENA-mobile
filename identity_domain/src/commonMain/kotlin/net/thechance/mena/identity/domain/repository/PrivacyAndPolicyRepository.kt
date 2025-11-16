package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.model.PrivacyAndPolicy

interface PrivacyAndPolicyRepository {
    suspend fun getPrivacyAndPolicy(): PrivacyAndPolicy
}