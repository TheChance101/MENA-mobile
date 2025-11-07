package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dto.addresses.response.AddressResponseDto
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.model.AddressInput
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AddressMapperTest {

    @Test
    fun `Address toDto should map latitude correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toDto()

        assertEquals(33.3152, result.latitude)
    }

    @Test
    fun `Address toDto should map longitude correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toDto()

        assertEquals(44.3661, result.longitude)
    }

    @Test
    fun `Address toDto should map addressLine correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toDto()

        assertEquals("Test Street", result.addressLine)
    }

    @Test
    fun `Address toDto should map Home addressType correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toDto()

        assertEquals("Home", result.addressType)
    }

    @Test
    fun `Address toDto should map Office addressType correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Office
        )

        val result = address.toDto()

        assertEquals("Office", result.addressType)
    }

    @Test
    fun `Address toDto should map Other addressType correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Other("Custom")
        )

        val result = address.toDto()

        assertEquals("Custom", result.addressType)
    }

    @Test
    fun `Address toDto should use provided id when given`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toDto(id = "test-id")

        assertEquals("test-id", result.id)
    }

    @Test
    fun `Address toDto should use provided isActive flag`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toDto(isActive = true)

        assertEquals(true, result.isActive)
    }

    @Test
    fun `AddressInput toDto should map latitude correctly`() {
        val addressInput = AddressInput(
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = addressInput.toDto()

        assertEquals(33.3152, result.latitude)
    }

    @Test
    fun `AddressInput toDto should map longitude correctly`() {
        val addressInput = AddressInput(
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = addressInput.toDto()

        assertEquals(44.3661, result.longitude)
    }

    @Test
    fun `AddressInput toDto should map addressLine correctly`() {
        val addressInput = AddressInput(
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = addressInput.toDto()

        assertEquals("Test Street", result.addressLine)
    }

    @Test
    fun `AddressInput toDto should map Home addressType correctly`() {
        val addressInput = AddressInput(
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = addressInput.toDto()

        assertEquals("Home", result.addressType)
    }

    @Test
    fun `AddressResponseDto toEntity should map id correctly`() {
        val addressDto = AddressResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = "Home",
            isActive = true
        )

        val result = addressDto.toEntity()

        assertEquals(Uuid.parse("550e8400-e29b-41d4-a716-446655440000"), result.id)
    }

    @Test
    fun `AddressResponseDto toEntity should map latitude correctly`() {
        val addressDto = AddressResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = "Home",
            isActive = true
        )

        val result = addressDto.toEntity()

        assertEquals(33.3152, result.latitude)
    }

    @Test
    fun `AddressResponseDto toEntity should map longitude correctly`() {
        val addressDto = AddressResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = "Home",
            isActive = true
        )

        val result = addressDto.toEntity()

        assertEquals(44.3661, result.longitude)
    }

    @Test
    fun `AddressResponseDto toEntity should map addressLine correctly`() {
        val addressDto = AddressResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = "Home",
            isActive = true
        )

        val result = addressDto.toEntity()

        assertEquals("Test Street", result.addressLine)
    }

    @Test
    fun `AddressResponseDto toEntity should map Home addressType correctly`() {
        val addressDto = AddressResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = "Home",
            isActive = true
        )

        val result = addressDto.toEntity()

        assertEquals(AddressType.Home, result.addressType)
    }

    @Test
    fun `AddressResponseDto toEntity should map Office addressType correctly`() {
        val addressDto = AddressResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = "Office",
            isActive = true
        )

        val result = addressDto.toEntity()

        assertEquals(AddressType.Office, result.addressType)
    }

    @Test
    fun `AddressResponseDto toEntity should map Other addressType correctly`() {
        val addressDto = AddressResponseDto(
            id = "550e8400-e29b-41d4-a716-446655440000",
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = "Custom",
            isActive = true
        )

        val result = addressDto.toEntity()

        assertEquals(AddressType.Other("Custom"), result.addressType)
    }
}

