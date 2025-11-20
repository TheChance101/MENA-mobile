package net.thechance.mena.identity.presentation.util

import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenUIState
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ValidationUtilsTest {

    @Test
    fun `isAddressInputValid should return true for Home addressType`() {
        val result = isAddressInputValid(AddressType.Home, null)

        assertTrue(result)
    }

    @Test
    fun `isAddressInputValid should return true for Office addressType`() {
        val result = isAddressInputValid(AddressType.Office, null)

        assertTrue(result)
    }

    @Test
    fun `isAddressInputValid should return true for Other addressType with non-blank otherAddressType`() {
        val result = isAddressInputValid(AddressType.Other("Custom"), "Custom")

        assertTrue(result)
    }

    @Test
    fun `isAddressInputValid should return false for Other addressType with blank otherAddressType`() {
        val result = isAddressInputValid(AddressType.Other("Custom"), "")

        assertFalse(result)
    }

    @Test
    fun `isAddressInputValid should return false for Other addressType with null otherAddressType`() {
        val result = isAddressInputValid(AddressType.Other("Custom"), null)

        assertFalse(result)
    }

    @Test
    fun `isAddressInputValid should return false for null addressType`() {
        val result = isAddressInputValid(null, null)

        assertFalse(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isAddressInputValid should return true for Home addressType`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.isAddressInputValid()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isAddressInputValid should return true for Office addressType`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Office,
                otherAddressType = null,
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.isAddressInputValid()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isAddressInputValid should return true for Other addressType with non-blank otherAddressType`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Other("Custom"),
                otherAddressType = "Custom",
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.isAddressInputValid()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isAddressInputValid should return false for Other addressType with blank otherAddressType`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Other("Custom"),
                otherAddressType = "",
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.isAddressInputValid()

        assertFalse(result)
    }

    @Test
    fun `AddEditLocationScreenUIState hasAddressChanged should return true when addressDetails changed`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "New Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            ),
            originalAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "Old Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.hasAddressChanged()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState hasAddressChanged should return true when addressType changed`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Office,
                otherAddressType = null,
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            ),
            originalAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.hasAddressChanged()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState hasAddressChanged should return true when otherAddressType changed`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Other("Custom"),
                otherAddressType = "Custom",
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            ),
            originalAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Other("Custom"),
                otherAddressType = "Old",
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.hasAddressChanged()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState hasAddressChanged should return true when coordinates changed`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            ),
            originalAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(34.0, 45.0)
            )
        )

        val result = state.hasAddressChanged()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState hasAddressChanged should return false when nothing changed`() {
        val addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = null,
            addressType = AddressType.Home,
            otherAddressType = null,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )
        val state = AddEditLocationScreenUIState(
            addressUIState = addressUIState,
            originalAddressUIState = addressUIState
        )

        val result = state.hasAddressChanged()

        assertFalse(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isSaveEnabled should return true in edit mode when address changed and valid`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
                addressType = AddressType.Office,
                otherAddressType = null,
                addressDetails = "New Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            ),
            originalAddressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "Old Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.isSaveEnabled()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isSaveEnabled should return false in edit mode when address not changed`() {
        val addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
            addressID = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            otherAddressType = null,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )
        val state = AddEditLocationScreenUIState(
            addressUIState = addressUIState,
            originalAddressUIState = addressUIState
        )

        val result = state.isSaveEnabled()

        assertFalse(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isSaveEnabled should return true in create mode when addressDetails not blank and valid`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "Test Street",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.isSaveEnabled()

        assertTrue(result)
    }

    @Test
    fun `AddEditLocationScreenUIState isSaveEnabled should return false in create mode when addressDetails is blank`() {
        val state = AddEditLocationScreenUIState(
            addressUIState = AddEditLocationScreenUIState.AddEditAddressUIState(
                addressID = null,
                addressType = AddressType.Home,
                otherAddressType = null,
                addressDetails = "",
                coordinates = CoordinatesUiState(33.3152, 44.3661)
            )
        )

        val result = state.isSaveEnabled()

        assertFalse(result)
    }

}