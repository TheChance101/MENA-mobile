package net.thechance.mena.faith.presentation.feature.quran.surah.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class SurahArgsImpl(
    savedStateHandle: SavedStateHandle,
) : SurahArgs {
    override val surahId: Int = savedStateHandle.toRoute<Route.SurahDetailsRoute>().surahId
    override val surahName: String = savedStateHandle.toRoute<Route.SurahDetailsRoute>().surahName
    override val ayahNumber: Int? = savedStateHandle.toRoute<Route.SurahDetailsRoute>().ayahNumber
        ?: savedStateHandle["ayahNumber"]


}