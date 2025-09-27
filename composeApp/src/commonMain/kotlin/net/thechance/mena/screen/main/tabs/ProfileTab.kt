package net.thechance.mena.screen.main.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.presentation.api.IdentityFeatureApiImpl

object ProfileTab : Tab {
    val identityFeatureApi: IdentityFeatureApi = IdentityFeatureApiImpl()
    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(index = 5u, title = "Profile")
        }

    @Composable
    override fun Content() {
        identityFeatureApi.ProfileScreenApi()
    }
}