package net.thechance.mena.faith.presentation.navigation

import net.thechance.mena.faith.presentation.feature.mosque.create.CreateMosqueEffect
import net.thechance.mena.faith.presentation.feature.mosque.create.CreateMosqueUiState


fun createNavigateToMapEffect(
    addressModel: CreateMosqueUiState? = null, onSuccess: (CreateMosqueUiState) -> Unit
): CreateMosqueEffect {
    return CreateMosqueEffect.NavigateToMap(addressModel, onSuccess)
}

//fun createNavigateToHomeEffect(): LoginScreenUIEffect {
//    return LoginScreenUIEffect.NavigateToHome
//}
//
//fun createNavigateToEditProfileEffect(
//    userInfo: User?
//): ProfileScreenUIEffect {
//    return ProfileScreenUIEffect.NavigateToEditProfileScreen(userInfo = userInfo)
//}