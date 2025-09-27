package net.thechance.mena.screen.main.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object DukanTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(index = 2u, title = "Dukan",)
        }

    @Composable
    override fun Content() {
        // Todo DukanFeatureApi.DukanScreen()
    }
}