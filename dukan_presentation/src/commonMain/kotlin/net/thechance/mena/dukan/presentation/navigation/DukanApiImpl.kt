package net.thechance.mena.dukan.presentation.navigation

import androidx.compose.runtime.Composable
import net.thechance.mena.dukan.api.DukanApi

class DukanApiImpl: DukanApi {
    @Composable
    override fun Launch() {
        DukanNavHost()
    }
}