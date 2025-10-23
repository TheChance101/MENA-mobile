package net.thechance.mena.identity.presentation.utils

import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenUIState
import kotlin.uuid.ExperimentalUuidApi

fun isAddressInputValid(addressType: AddressType?, otherAddressType: String?): Boolean {
    return when (addressType) {
        AddressType.Home -> true
        AddressType.Office -> true
        is AddressType.Other -> !otherAddressType.isNullOrBlank()
        else -> false
    }
}


fun AddEditLocationScreenUIState.isAddressInputValid(): Boolean {
    return isAddressInputValid(
        addressUIState.addressType,
        addressUIState.otherAddressType
    )
}

fun AddEditLocationScreenUIState.hasAddressChanged(): Boolean {
    return addressUIState.addressDetails != originalAddressUIState.addressDetails ||
            addressUIState.addressType != originalAddressUIState.addressType ||
            addressUIState.otherAddressType != originalAddressUIState.otherAddressType ||
            addressUIState.coordinates != originalAddressUIState.coordinates
}

@OptIn(ExperimentalUuidApi::class)
fun AddEditLocationScreenUIState.isSaveEnabled(): Boolean {
    val isEditMode = addressUIState.addressID != null
    
    return if (isEditMode) {
        hasAddressChanged() && isAddressInputValid()
    } else {
        addressUIState.addressDetails.isNotBlank() && isAddressInputValid()
    }
}

fun validatePasswordConfirmation(password: String, confirmPassword: String): String? {
    return if (password != confirmPassword) {
        "Confirm password doesn't match the new password"
    } else null
}