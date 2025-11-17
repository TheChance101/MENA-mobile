package net.thechance.mena.identity.presentation.core.mapper

import net.thechance.mena.identity.presentation.feature.authenticationFlow.login.LoginScreenUIEffect
import net.thechance.mena.identity.presentation.feature.locationFlow.addEditLocation.AddEditLocationScreenUIEffect
import net.thechance.mena.identity.presentation.feature.locationFlow.shared.AddressUIState
import net.thechance.mena.identity.presentation.feature.profileFlow.profile.ProfileScreenUIEffect

fun createNavigateToMapEffect(
    addressModel: AddressUIState? = null, onSuccess: (AddressUIState) -> Unit
): AddEditLocationScreenUIEffect {
    return AddEditLocationScreenUIEffect.NavigateToMap(addressModel, onSuccess)
}

fun createNavigateToHomeEffect(): LoginScreenUIEffect {
    return LoginScreenUIEffect.NavigateToHome
}

fun createNavigateToEditProfileEffect(): ProfileScreenUIEffect {
    return ProfileScreenUIEffect.NavigateToEditProfileScreen
}