package net.thechance.mena.identity.api

import androidx.compose.runtime.Composable

interface IdentityFeatureApi {
    @Composable
    fun ProfileTabEntry()

    @Composable
    fun LoginFlow()

    @Composable
    fun NavigateToAddressesScreen()
}