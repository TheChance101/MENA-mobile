package net.thechance.mena.faith.presentation

import androidx.compose.runtime.Composable
import net.thechance.mena.faith.api.FaithApi
import net.thechance.mena.faith.presentation.navigation.FaithNavigation
import net.thechance.mena.faith.presentation.navigation.SurahNavHost

class FaithApiImpl : FaithApi {
    @Composable
    override fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit) {
        FaithNavigation(updateBottomNavigationVisibility = updateBottomNavigationVisibility)
    }

    @Composable
    override fun NavigateToSurahScreen(
        surahId: Int,
        ayahNumber: Int,
        onNavigateBack: () -> Unit
    ) {
        SurahNavHost(
            surahId = surahId,
            ayahNumber = ayahNumber,
            onNavigateBack = onNavigateBack
        )
    }
}