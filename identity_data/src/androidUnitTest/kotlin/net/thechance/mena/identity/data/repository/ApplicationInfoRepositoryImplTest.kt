package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.ContactInfoResponseDto
import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.PrivacyAndPolicyResponseDto
import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.SectionResponseDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.utils.mockHttpClient
import net.thechance.mena.identity.domain.service.LocalizationService
import net.thechance.mena.identity.domain.util.AppLanguage
import org.junit.Test
import kotlin.test.assertEquals

class ApplicationInfoRepositoryImplTest {

    private lateinit var client: HttpClient
    private val localizationService = mockk<LocalizationService>()
    private lateinit var applicationInfoRepositoryImpl: ApplicationInfoRepositoryImpl

    @Test
    fun `getPrivacyAndPolicy() should not throw exception when server returns 200`() = runTest {
        coEvery { localizationService.getCurrentLanguage() } returns AppLanguage.ENGLISH
        client = mockHttpClient(fakePrivacyAndPolicyResponseDto)

        applicationInfoRepositoryImpl = ApplicationInfoRepositoryImpl(client, localizationService)

        applicationInfoRepositoryImpl.getPrivacyAndPolicy()
    }

    @Test
    fun `getContactInfo() should return contact info when server returns 200`() = runTest {
        client = mockHttpClient(fakeContactInfoResponseDto)
        applicationInfoRepositoryImpl = ApplicationInfoRepositoryImpl(client, localizationService)

        val result = applicationInfoRepositoryImpl.getContactInfo()

        assertEquals(fakeContactInfoResponseDto.toDomain(), result)
    }

    private val fakeContactInfoResponseDto = ContactInfoResponseDto(
        email = "test@email.com",
        phoneNumber = "123456789",
        facebookAccount = "https://facebook.com/test"
    )

    private val fakePrivacyAndPolicyResponseDto = PrivacyAndPolicyResponseDto(
        updateDate = "11/11/2025",
        sections = listOf(
            SectionResponseDto(
                title = "What is Lorem Ipsum?",
                content = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"
            ),
            SectionResponseDto(
                title = "What is Lorem Ipsum?",
                content = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"
            )
        )
    )
}
