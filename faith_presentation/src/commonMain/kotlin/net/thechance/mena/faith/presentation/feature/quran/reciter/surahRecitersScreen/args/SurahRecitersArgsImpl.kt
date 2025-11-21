package net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class SurahRecitersArgsImpl(savedStateHandle: SavedStateHandle) : SurahRecitersArgs {
    private val args = savedStateHandle.toRoute<Route.SurahRecitersRoute>()
    override val surahId: Int? = args.surahId
}
