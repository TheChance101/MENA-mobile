package net.thechance.mena.faith.presentation.feature.quran.surah.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class SurahArgsImpl(
    savedStateHandle: SavedStateHandle,
) : SurahArgs {
    private val surahScreenArgs = savedStateHandle.toRoute<Route.SurahDetailsRoute>()

    override val surahId: Int = surahScreenArgs.surahId
    override val ayahNumber: Int? = surahScreenArgs.ayahNumber
        ?: savedStateHandle["ayahNumber"]


}