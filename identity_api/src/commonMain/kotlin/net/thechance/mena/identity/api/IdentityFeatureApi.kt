package net.thechance.mena.identity.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface IdentityFeatureApi {
    @Composable
    fun ProfileTabEntry(updateBottomNavigationVisibility:(Boolean)-> Unit)

    @Composable
    fun LoginFlow()

    @Composable
    fun NavigateToAddressesScreen(onNavigateBack: (() -> Unit)?)
}