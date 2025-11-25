package net.thechance.mena.faith.api

import androidx.compose.runtime.Composable

interface FaithApi {
    @Composable
    fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit)

    @Composable
    fun NavigateToSurahScreen(surahId: Int, ayahNumber: Int,onNavigateBack: () -> Unit)
}