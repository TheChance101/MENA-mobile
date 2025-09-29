package net.thechance.mena.faith.presentation

import androidx.compose.runtime.Composable
import net.thechance.mena.faith.api.FaithApi
import net.thechance.mena.faith.presentation.navigation.FaithNavigation

class FaithApiImpl: FaithApi {
@Composable
    override fun TabEntry() {
        FaithNavigation()
    }
}