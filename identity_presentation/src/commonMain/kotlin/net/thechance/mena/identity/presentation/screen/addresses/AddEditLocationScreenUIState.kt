package net.thechance.mena.identity.presentation.screen.addresses

data class AddLocationScreenUIState(
    val addressID: String? = null,
    val latitude: Double = 28.0,
    val longitude: Double = 29.0,
    val address: String = "",
    val addressType: AddressType? = null,
    val otherAddress: String? = null,
    val originalAddress: String = "",
    val originalAddressType: AddressType? = null,
    val originalOtherAddress: String? = null,
    val isActive: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

enum class AddressType {
    Home,
    Office,
    Other
}