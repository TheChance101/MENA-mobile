package net.thechance.mena.identity.presentation.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.presentation.screen.login.LoginScreen

class IdentityFeatureApiImpl : IdentityFeatureApi {
    @Composable
    override fun ProfileTabEntry() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "ProfileScreen", Theme.typography.body.medium)
        }
    }

    @Composable
    override fun LoginFlow() {
        Navigator(LoginScreen())
    }
}