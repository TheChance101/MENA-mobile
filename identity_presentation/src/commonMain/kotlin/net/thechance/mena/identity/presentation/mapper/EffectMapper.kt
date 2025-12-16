package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenUIEffect
import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import net.thechance.mena.identity.presentation.screen.login.LoginScreenUIEffect
import net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.ProfileScreenUIEffect
import net.thechance.mena.identity.presentation.screen.profile.shared.UserUIState

fun createNavigateToMapEffect(
    addressModel: AddressUIState? = null, onSuccess: (AddressUIState) -> Unit
): AddEditLocationScreenUIEffect {
    return AddEditLocationScreenUIEffect.NavigateToMap(addressModel, onSuccess)
}

fun createNavigateToHomeEffect(): LoginScreenUIEffect {
    return LoginScreenUIEffect.NavigateToHome
}

fun createNavigateToEditProfileEffect(
    userInfo: UserUIState
): ProfileScreenUIEffect {
    return ProfileScreenUIEffect.NavigateToEditProfileScreen(userInfo = userInfo)
}