package net.thechance.mena.faith.presentation.feature.mosque.create

sealed interface CreateMosqueEffect {
    object NavigateBack : CreateMosqueEffect
    data object NavigateToUploadImageRoute : CreateMosqueEffect
    data object NavigateToAddressesScreen : CreateMosqueEffect
    data class NavigateToMap(
        val addressModel: CreateMosqueUiState? = null,
        val onUpdateLocation: (CreateMosqueUiState) -> Unit
    ) : CreateMosqueEffect
}