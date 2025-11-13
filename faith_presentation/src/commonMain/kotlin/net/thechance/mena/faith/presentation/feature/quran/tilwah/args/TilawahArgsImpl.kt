package net.thechance.mena.faith.presentation.feature.quran.tilwah.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class TilawahArgsImpl(
    savedStateHandle: SavedStateHandle,
) : TilawahArgs {
    private val downloadedRecitersRouteArgs = savedStateHandle.toRoute<Route.DownloadedRecitersRoute>()
    override val surahId: Int? = downloadedRecitersRouteArgs.surahId
    override val isSwipeToDeleteEnabled: Boolean = downloadedRecitersRouteArgs.isCardsSwipable
    override val isDownloadButtonShown: Boolean = downloadedRecitersRouteArgs.isDownloadShown
}
