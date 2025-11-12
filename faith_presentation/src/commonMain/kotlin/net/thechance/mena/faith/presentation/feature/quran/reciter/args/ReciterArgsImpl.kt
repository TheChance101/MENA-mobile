package net.thechance.mena.faith.presentation.feature.quran.reciter.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.faith.presentation.navigation.Route

class ReciterArgsImpl(
    savedStateHandle: SavedStateHandle
) : ReciterArgs {
    private val reciterSearchArgs = savedStateHandle.toRoute<Route.ReciterSearch>()

    override val surahId: Int? = reciterSearchArgs.surahId

}