package net.thechance.mena.faith.presentation.feature.quran.search.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class SearchArgsImpl(
    savedStateHandle: SavedStateHandle,
) : ISearchArgs {
    override val surahId: Int? = savedStateHandle.toRoute<Route.SearchRoute>().surahId
    override val surahName: String? = savedStateHandle.toRoute<Route.SearchRoute>().surahName
}