package net.thechance.mena.identity.presentation.api

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreen
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import net.thechance.mena.identity.presentation.screen.register.accountCreated.AccountCreatedScreen
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreen
import net.thechance.mena.identity.presentation.screen.register.enterName.EnterNameScreen

class IdentityFeatureApiImpl : IdentityFeatureApi {
    @Composable
    override fun ProfileTabEntry() {
        Navigator(ProfileScreen())
    }

    @Composable
    override fun LoginFlow() {
        Navigator(LoginScreen())
    }

    @Composable
    override fun NavigateToAddressesScreen() {
        Navigator(AddressesScreen())
    }
}