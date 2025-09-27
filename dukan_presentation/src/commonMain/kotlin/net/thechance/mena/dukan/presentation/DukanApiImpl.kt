package net.thechance.mena.dukan.presentation

import androidx.compose.runtime.Composable
import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.dukan.presentation.navigation.DukanNavHost

class DukanApiImpl: DukanApi {
    @Composable
    override fun TabEntry() {
        DukanNavHost()
    }
}