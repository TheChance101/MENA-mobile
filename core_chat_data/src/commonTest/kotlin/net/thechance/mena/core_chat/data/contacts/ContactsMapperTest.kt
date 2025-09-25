package net.thechance.mena.core_chat.data.contacts

import kotlin.test.Test
import kotlin.test.assertFailsWith
import assertk.assertThat
import assertk.assertions.*
import net.thechance.mena.core_chat.data.contacts.dto.ContactDto
import net.thechance.mena.core_chat.data.contacts.fakes.createContactDto
import net.thechance.mena.core_chat.data.contacts.fakes.createDeviceContact
import net.thechance.mena.core_chat.data.contacts.fakes.createPagedDataDto
import net.thechance.mena.core_chat.data.shared.dto.PagedDataDto
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException


class ContactMappersTest {

    @Test
    fun `toPagedListOfContacts should throw ContactsFetchFailedException when Dto is null`() {
        assertFailsWith<ContactsFetchFailedException> {
            (null as PagedDataDto<ContactDto>?).toPagedListOfContacts()
        }
    }

    @Test
    fun `toPagedListOfContacts should map contacts and mark as last page when PageNumber equals TotalPages`() {
        val dto = createPagedDataDto(
            data = listOf(
                createContactDto(
                firstName = "Bilal",
                phone = "123"
            )
            ),
            pageNumber = 5,
            totalItems = 1,
            totalPages = 5
        )

        val result = dto.toPagedListOfContacts()

        assertThat(result.data).hasSize(1)
        assertThat(result.isLastPage).isTrue()
    }

    @Test
    fun `toPagedListOfContacts should return empty list when Data is null`() {
        val dto = createPagedDataDto(
            data = null,
            pageNumber = 1,
            totalItems = 5,
            totalPages = 10
        )

        val result = dto.toPagedListOfContacts()

        assertThat(result.data).isEmpty()
    }

    @Test
    fun `toPagedListOfContacts should set totalItems to 0 when TotalItems is null`() {
        val dto = createPagedDataDto(
            data = listOf(
                createContactDto(
                firstName = "Bilal",
                lastName = "Azzam",
                phone = "789"
            )
            ),
            pageNumber = 1,
            totalItems = null,
            totalPages = 5
        )

        val result = dto.toPagedListOfContacts()

        assertThat(result.totalItems).isEqualTo(0)
        assertThat(result.data).hasSize(1)
    }

    @Test
    fun `toPagedListOfContacts should treat missing pageNumber as 0 when PageNumber is null`() {
        val dto = createPagedDataDto(
            data = emptyList(),
            pageNumber = null,
            totalItems = 10,
            totalPages = 5
        )

        val result = dto.toPagedListOfContacts()

        assertThat(result.isLastPage).isFalse()
    }

    @Test
    fun `toPagedListOfContacts should treat missing totalPages as 0 when TotalPages is null`() {
        val dto = createPagedDataDto(
            data = emptyList(),
            pageNumber = 1,
            totalItems = 10,
            totalPages = null
        )

        val result = dto.toPagedListOfContacts()

        assertThat(result.isLastPage).isTrue()
    }

    @Test
    fun `toDomain should return default values when Fields are null`() {
        val contact = createContactDto().toDomain()

        assertThat(contact.firstName).isEqualTo("")
        assertThat(contact.lastName).isEqualTo("")
        assertThat(contact.phone).isEqualTo("")
        assertThat(contact.isMenaUser).isFalse()
        assertThat(contact.imageUrl).isNull()
    }

    @Test
    fun `toDomain should map all fields correctly when Fields are non null`() {
        val dto = createContactDto(
            firstName = "Bilal",
            lastName = "Azzam",
            phone = "456",
            isMenaUser = true,
            imageUrl = "url"
        )

        val contact = dto.toDomain()

        assertThat(contact.firstName).isEqualTo("Bilal")
        assertThat(contact.lastName).isEqualTo("Azzam")
        assertThat(contact.phone).isEqualTo("456")
        assertThat(contact.isMenaUser).isTrue()
        assertThat(contact.imageUrl).isEqualTo("url")
    }


    @Test
    fun `toListOfContactCreationRequestDto should return empty list when DeviceContact has no phones`() {
        val deviceContact = createDeviceContact(phoneNumbers = emptyList())

        val result = deviceContact.toListOfContactCreationRequestDto()

        assertThat(result).isEmpty()
    }

    @Test
    fun `toListOfContactCreationRequestDto should map each phone when DeviceContact has multiple phones`() {
        val deviceContact = createDeviceContact(phoneNumbers = listOf("123", "456"))

        val result = deviceContact.toListOfContactCreationRequestDto()

        assertThat(result.map { it.phone }).containsExactly("123", "456")
    }

    @Test
    fun `toListOfContactCreationRequestDto should flatten all phones when MultipleDeviceContacts provided`() {
        val contacts = listOf(
            createDeviceContact(id = "1", phoneNumbers = listOf("111")),
            createDeviceContact(id = "2", phoneNumbers = listOf("222", "333"))
        )

        val result = contacts.toListOfContactCreationRequestDto()

        assertThat(result.map { it.phone }).containsExactly("111", "222", "333")
    }

    @Test
    fun `toListOfContactCreationRequestDto should use empty strings when FirstName and LastName are null`() {
        val deviceContact = createDeviceContact(
            firstName = null,
            lastName = null,
            phoneNumbers = listOf("999")
        )

        val result = deviceContact.toListOfContactCreationRequestDto()

        assertThat(result).hasSize(1)
        assertThat(result.first().firstName).isEqualTo("")
        assertThat(result.first().lastName).isEqualTo("")
        assertThat(result.first().phone).isEqualTo("999")
    }
}
