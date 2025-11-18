package net.thechance.mena.faith.presentation.feature.quran.tilwah.component.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class TilawahSurahArgsImpl(
    savedStateHandle: SavedStateHandle
) : TilawahSurahArgs {
    private val downloadedSurah = savedStateHandle.toRoute<Route.DownloadedRecitersRoute>()
    override val surahId: Int? = downloadedSurah.surahId
    override val isSwipeToDeleteEnabled: Boolean = downloadedSurah.isCardsSwipable
}
