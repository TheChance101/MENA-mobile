package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenUIEffect
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import net.thechance.mena.identity.presentation.screen.login.LoginScreenUIEffect
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreenUIEffect

fun createNavigateToMapEffect(
    addressModel: AddressUIState? = null, onSuccess: (AddressUIState) -> Unit
): AddEditLocationScreenUIEffect {
    return AddEditLocationScreenUIEffect.NavigateToMap(addressModel, onSuccess)
}

fun createNavigateToHomeEffect(): LoginScreenUIEffect {
    return LoginScreenUIEffect.NavigateToHome
}

fun createNavigateToEditProfileEffect(
    userInfo: User?
): ProfileScreenUIEffect {
    return ProfileScreenUIEffect.NavigateToEditProfileScreen(userInfo = userInfo)
}