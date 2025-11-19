package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.ContactInfoResponseDto
import org.junit.Test
import kotlin.test.assertEquals

class ContactInfoMapperTest {

    @Test
    fun `toDomain should convert ContactInfoResponseDto to ContactInfo correctly`() {
        val contactInfoDto = ContactInfoResponseDto(
            email = "test@mena.com",
            phoneNumber = "+123456789",
            facebookAccount = "https://facebook.com/mena"
        )

        val contactInfo = contactInfoDto.toDomain()
        assertEquals(contactInfoDto.email, contactInfo.email)
        assertEquals(contactInfoDto.phoneNumber, contactInfo.phoneNumber)
        assertEquals(contactInfoDto.facebookAccount, contactInfo.facebookAccount)
    }
}
