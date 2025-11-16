package net.thechance.mena.faith.presentation.feature.quran.sur

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class SurViewModel(
    private val quranRepository: QuranRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<SurUiState, SurEffect>(SurUiState()), SurInteractionListener {

    init {
        initializeSur()
    }

    override fun onSurahClick(surahId: Int) =
        sendEffect(SurEffect.NavigateToSurahDetails(surahId))

    override fun onBackClick() = sendEffect(SurEffect.NavigateBack)

    override fun onBookmarkClick() = sendEffect(SurEffect.NavigateToBookmark)
    override fun onSearchClick() = sendEffect(SurEffect.NavigateToSearch)

    private fun initializeSur() {
        tryToExecute(
            execute = { quranRepository.getSur() },
            onSuccess = { sur -> handleSuccessState(sur) },
            dispatcher = dispatcher
        )
    }

    private fun handleSuccessState(sur: List<Surah>) =
        updateState { it.copy(sur = sur.map { surah -> surah.toUi() }) }

}
