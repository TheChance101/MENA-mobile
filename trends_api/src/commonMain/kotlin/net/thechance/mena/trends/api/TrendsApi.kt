package net.thechance.mena.trends.api

import androidx.compose.runtime.Composable

interface TrendsApi {
    @Composable
    fun TabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit)
}