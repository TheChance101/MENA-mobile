@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.data.contacts

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import net.thechance.mena.core_chat.data.contacts.fakes.createContactDto
import net.thechance.mena.core_chat.data.contacts.fakes.createDeviceContact
import net.thechance.mena.core_chat.data.contacts.fakes.createPagedDataDto
import net.thechance.mena.core_chat.data.source.remote.dto.ContactDto
import net.thechance.mena.core_chat.data.source.remote.dto.PagedDataDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toDomain
import net.thechance.mena.core_chat.data.source.remote.mapper.toListOfContactCreationRequestDto
import net.thechance.mena.core_chat.data.source.remote.mapper.toPagedListOfContacts
import net.thechance.mena.core_chat.domain.exception.ContactsFetchFailedException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


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
            data = emptyList(),
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
            totalItems = 15,
            pageNumber = 1,
            totalPages = 5
        )

        val result = dto.toPagedListOfContacts()

        assertThat(result.totalItems).isEqualTo(15)
        assertThat(result.data).hasSize(1)
    }

    @Test
    fun `toPagedListOfContacts should treat missing pageNumber as 0 when PageNumber is null`() {
        val dto = createPagedDataDto(
            data = emptyList(),
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
        )

        val result = dto.toPagedListOfContacts()

        assertThat(result.isLastPage).isFalse()
    }

    @Test
    fun `toDomain should return default values when Fields are null`() {
        val contact = createContactDto().toDomain()

        assertThat(contact.firstName).isEqualTo("")
        assertThat(contact.lastName).isEqualTo("")
        assertThat(contact.phone).isEqualTo("")
        assertThat(contact.menaUserId).isEqualTo(null)
        assertThat(contact.imageUrl).isNull()
    }

    @Test
    fun `toDomain should map all fields correctly when Fields are non null`() {
        val dto = createContactDto(
            firstName = "Bilal",
            lastName = "Azzam",
            phone = "456",
            menaUserId = Uuid.random().toString(),
            imageUrl = "url"
        )

        val contact = dto.toDomain()

        assertThat(contact.firstName).isEqualTo("Bilal")
        assertThat(contact.lastName).isEqualTo("Azzam")
        assertThat(contact.phone).isEqualTo("456")
        assertThat(contact.menaUserId).isNotNull()
        assertThat(contact.imageUrl).isEqualTo("https://menastorage.fra1.cdn.digitaloceanspaces.com/identity/profile/image/url")
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
