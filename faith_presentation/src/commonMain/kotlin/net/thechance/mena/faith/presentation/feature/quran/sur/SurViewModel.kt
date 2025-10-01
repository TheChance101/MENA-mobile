package net.thechance.mena.faith.presentation.feature.quran.sur

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel

class SurViewModel(
    private val quranRepository: QuranRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<SurScreenState, SurEffect>(SurScreenState()), SurInteractionListener {

    init {
        initializeSur()
    }

    override fun onSurahClick(surahId: Int, surahName: String) =
        sendEffect(SurEffect.NavigateToSurahDetails(surahId, surahName))

    override fun onBackClick() = sendEffect(SurEffect.NavigateBack)

    override fun onBookmarkClick() = sendEffect(SurEffect.NavigateToBookmark)

    private fun initializeSur() {
        tryToExecute(
            onStart = { setLoadingState(true) },
            execute = { quranRepository.getAllSur() },
            onSuccess = { sur -> handleSuccessState(sur) },
            onError = { throwable -> handleErrorState(throwable) },
            onFinally = { setLoadingState(false) },
            dispatcher = dispatcher
        )
    }

    private fun setLoadingState(isLoading: Boolean) = updateState { it.copy(isLoading = isLoading) }

    private fun handleErrorState(throwable: Throwable) =
        updateState { it.copy(errorMessage = "${throwable.message}") }

    private fun handleSuccessState(sur: List<Surah>) =
        updateState { it.copy(sur = sur.map { surah -> surah.toUi() }) }
}
