package net.thechance.mena.identity.presentation.screen.addresses.myAddresses

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.delete_address_description
import mena.identity_presentation.generated.resources.delete_address_title
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class MyAddressesScreenUIState(
    val addresses: List<AddressUIState> = emptyList(),
    val deleteDialogUIState: DeleteDialogUIState = DeleteDialogUIState(),
    val errorMessage: StringResource? = null,
    val animateToCurrentLocation: Boolean = false,
    val isLoading: Boolean = true,
    val isAddingNewAddress: Boolean = false,
    val editedAddressId: Uuid? = null
)

@OptIn(ExperimentalUuidApi::class)
data class DeleteDialogUIState(
    val title: StringResource = Res.string.delete_address_title,
    val description: StringResource = Res.string.delete_address_description,
    val addressId: Uuid? = null,
    val isVisible: Boolean = false
)