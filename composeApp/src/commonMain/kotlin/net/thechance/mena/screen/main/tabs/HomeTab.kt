package net.thechance.mena.screen.main.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(index = 1u, title = "Home")
        }

    @Composable
    override fun Content() {
        // Todo ChatFeatureApi.HomeScreen()
    }
}