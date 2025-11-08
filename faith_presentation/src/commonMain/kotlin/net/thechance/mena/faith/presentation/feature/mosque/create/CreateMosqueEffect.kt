package net.thechance.mena.faith.presentation.feature.mosque.create

internal sealed interface CreateMosqueEffect {
    object NavigateBack : CreateMosqueEffect
    data object NavigateToUploadImageRoute : CreateMosqueEffect
    data object NavigateToAddressesScreen : CreateMosqueEffect

}