package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class DownloadedRecitersArgsImpl(savedStateHandle: SavedStateHandle) : DownloadedRecitersArgs {
    private val args = savedStateHandle.toRoute<Route.DownloadedRecitersRoute>()
    override val surahId: Int? = args.surahId
}


