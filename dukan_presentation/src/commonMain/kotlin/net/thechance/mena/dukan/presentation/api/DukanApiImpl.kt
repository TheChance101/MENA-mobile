package net.thechance.mena.dukan.presentation.api

import androidx.compose.runtime.Composable
import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.dukan.presentation.navigation.DukanNavHost

class DukanApiImpl: DukanApi {
    @Composable
    override fun Launch() {
        DukanNavHost()
    }
}