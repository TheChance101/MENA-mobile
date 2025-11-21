package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenUIState
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AddressMapperTest {

    @Test
    fun `Address toUiState should map id correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toUiState(id = address.id)

        assertEquals(address.id, result.id)
    }

    @Test
    fun `Address toUiState should map addressType correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toUiState()

        assertEquals(AddressType.Home, result.addressType)
    }

    @Test
    fun `Address toUiState should map isMainAddress correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toUiState(isMainAddress = true)

        assertEquals(true, result.isMainAddress)
    }

    @Test
    fun `Address toUiState should map addressDetails correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toUiState()

        assertEquals("Test Street", result.addressDetails)
    }

    @Test
    fun `Address toUiState should map coordinates latitude correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toUiState()

        assertEquals(33.3152, result.coordinates.latitude)
    }

    @Test
    fun `Address toUiState should map coordinates longitude correctly`() {
        val address = Address(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Test Street",
            addressType = AddressType.Home
        )

        val result = address.toUiState()

        assertEquals(44.3661, result.coordinates.longitude)
    }

    @Test
    fun `AddressUIState toEntity should map id correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toEntity()

        assertEquals(Uuid.parse("550e8400-e29b-41d4-a716-446655440000"), result.id)
    }

    @Test
    fun `AddressUIState toEntity should map latitude correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toEntity()

        assertEquals(33.3152, result.latitude)
    }

    @Test
    fun `AddressUIState toEntity should map longitude correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toEntity()

        assertEquals(44.3661, result.longitude)
    }

    @Test
    fun `AddressUIState toEntity should map addressLine correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toEntity()

        assertEquals("Test Street", result.addressLine)
    }

    @Test
    fun `AddressUIState toEntity should map addressType correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toEntity()

        assertEquals(AddressType.Home, result.addressType)
    }

    @Test
    fun `AddressUIState toAddressInput should map latitude correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toAddressInput()

        assertEquals(33.3152, result.latitude)
    }

    @Test
    fun `AddressUIState toAddressInput should map longitude correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toAddressInput()

        assertEquals(44.3661, result.longitude)
    }

    @Test
    fun `AddressUIState toAddressInput should map addressLine correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toAddressInput()

        assertEquals("Test Street", result.addressLine)
    }

    @Test
    fun `AddressUIState toAddressInput should map addressType correctly`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addressUIState.toAddressInput()

        assertEquals(AddressType.Home, result.addressType)
    }

    @Test
    fun `AddEditAddressUIState toAddressInput should map latitude correctly`() {
        val addEditAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = null,
            addressType = AddressType.Home,
            otherAddressType = null,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addEditAddressUIState.toAddressInput()

        assertEquals(33.3152, result.latitude)
    }

    @Test
    fun `AddEditAddressUIState toAddressInput should map longitude correctly`() {
        val addEditAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = null,
            addressType = AddressType.Home,
            otherAddressType = null,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addEditAddressUIState.toAddressInput()

        assertEquals(44.3661, result.longitude)
    }

    @Test
    fun `AddEditAddressUIState toAddressInput should map addressLine correctly`() {
        val addEditAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = null,
            addressType = AddressType.Home,
            otherAddressType = null,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addEditAddressUIState.toAddressInput()

        assertEquals("Test Street", result.addressLine)
    }

    @Test
    fun `AddEditAddressUIState toAddressInput should use Home when addressType is null`() {
        val addEditAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = null,
            addressType = null,
            otherAddressType = null,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addEditAddressUIState.toAddressInput()

        assertEquals(AddressType.Home, result.addressType)
    }

    @Test
    fun `AddEditAddressUIState toAddressInput should use Other when otherAddressType is not blank`() {
        val addEditAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = null,
            addressType = AddressType.Other(""),
            otherAddressType = "Custom",
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addEditAddressUIState.toAddressInput()

        assertIs<AddressType.Other>(result.addressType)
    }

    @Test
    fun `AddEditAddressUIState toAddressInput should use addressType when otherAddressType is blank`() {
        val addEditAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = null,
            addressType = AddressType.Office,
            otherAddressType = "",
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )

        val result = addEditAddressUIState.toAddressInput()

        assertEquals(AddressType.Office, result.addressType)
    }
}