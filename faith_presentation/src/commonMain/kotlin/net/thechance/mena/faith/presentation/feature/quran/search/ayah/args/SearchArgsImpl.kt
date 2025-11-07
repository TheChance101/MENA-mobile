package net.thechance.mena.faith.presentation.feature.quran.search.ayah.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class SearchArgsImpl(
    savedStateHandle: SavedStateHandle,
) : SearchArgs {
    private val searchRouteArgs = savedStateHandle.toRoute<Route.SearchRoute>()

    override val surahId: Int? = searchRouteArgs.surahId
    override val surahName: String? = searchRouteArgs.surahName
}