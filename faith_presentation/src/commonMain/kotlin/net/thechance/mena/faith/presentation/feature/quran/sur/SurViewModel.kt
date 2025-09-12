package net.thechance.mena.faith.presentation.feature.quran.sur

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SurViewModel : ViewModel(), SurContract {
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

    }
}
