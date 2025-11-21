package net.thechance.mena.trends.presentation.navigation

import androidx.compose.runtime.Composable
import net.thechance.mena.trends.api.TrendsApi
import org.koin.core.annotation.Single

@Single(binds = [TrendsApi::class])
class TrendsApiImpl() : TrendsApi {

    @Composable
    override fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit) {
        TrendsNavHost(
            updateBottomNavigationVisibility = updateBottomNavigationVisibility
        )
    }
}