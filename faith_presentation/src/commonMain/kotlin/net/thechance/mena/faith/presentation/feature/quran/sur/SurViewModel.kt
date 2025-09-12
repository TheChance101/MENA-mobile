package net.thechance.mena.faith.presentation.feature.quran.sur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository

class SurViewModel(
    val repository: QuranRepository
) : ViewModel(), SurContract {
    private val _state = MutableStateFlow(SurUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SurEffect?>()
    val effect = _effect.asSharedFlow()

    init {
        initializeSur()
    }

    override fun onSurahClicked(id: Int) = emitEffect(SurEffect.SurahDetailsNavigation(id))

    override fun onBackClicked() = emitEffect(SurEffect.BackNavigation)

    override fun onBookmarkClicked() = emitEffect(SurEffect.BookMarkNavigation)

    private fun emitEffect(effect: SurEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun initializeSur() {
        viewModelScope.launch {
            runCatching {
                setLoadingState()
                repository.getAllSurah()
            }.onSuccess(::handleSuccessState).onFailure(::handleErrorState)
        }
    }

    private fun setLoadingState() {
        _state.update {
            it.copy(isLoading = true)
        }
    }

    private fun handleErrorState(throwable: Throwable) {
        _state.update {
            it.copy(
                errorMessage = "${throwable.message}",
                isLoading = false
            )
        }
    }

    private fun handleSuccessState(sur: List<Surah>) {
        _state.update {
            it.copy(
                sur = sur.map { surah ->
                    surah.toUi()
                },
                isLoading = false
            )
        }
    }
}
