package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.ContactInfoResponseDto
import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.PrivacyAndPolicyResponseDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.model.ContactInfo
import net.thechance.mena.identity.domain.model.PrivacyAndPolicy
import net.thechance.mena.identity.domain.repository.ApplicationInfoRepository
import net.thechance.mena.identity.domain.service.LocalizationService

class ApplicationInfoRepositoryImpl(
    private val client: HttpClient,
    private val localizationService: LocalizationService
): ApplicationInfoRepository {

    override suspend fun getPrivacyAndPolicy(): PrivacyAndPolicy {
        val response: PrivacyAndPolicyResponseDto =  safeWrapper {
          client.getJson(PRIVACY_AND_POLICY_PATH ,
              headerParams = mapOf(ACCEPT_LANGUAGE to localizationService.getCurrentLanguage().iso))
       }
      return  response.toDomain()
    }
    override suspend fun getContactInfo(): ContactInfo {
        return safeWrapper<ContactInfoResponseDto> {
            client.getJson(CONTACT_INFO_PATH)
        }.toDomain()
    }
   private companion object{
       const val ACCEPT_LANGUAGE = "Accept-Language"
        const val PRIVACY_AND_POLICY_PATH = "/identity/settings/privacy-and-policy"
        const val CONTACT_INFO_PATH = "/identity/settings/contact-info"
   }

}