package net.thechance.mena.identity.presentation.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.api.IdentityFeatureApi

class IdentityFeatureApiImpl : IdentityFeatureApi {
    @Composable
    override fun ProfileScreenApi() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "ProfileScreen", Theme.typography.body.medium)
        }
    }
}