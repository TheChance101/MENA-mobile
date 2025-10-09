package net.thechance.mena.identity.presentation.api

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreen

class IdentityFeatureApiImpl : IdentityFeatureApi {
    @Composable
    override fun ProfileTabEntry() {
        Navigator(ProfileScreen())
    }

    @Composable
    override fun LoginFlow() {
        Navigator(LoginScreen())
    }
}