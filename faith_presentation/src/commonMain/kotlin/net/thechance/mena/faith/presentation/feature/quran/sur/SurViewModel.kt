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
        viewModelScope.launch {
            _state.update {
                it.copy(
                    sur = getFakeData().map { surah ->
                        surah.toUi()
                    }
                )
            }
        }
    }

    private fun getFakeData(): List<Surah> {
        return listOf(
            Surah(
                id = 1,
                order = Surah.SurahOrder.AlFatihah,
                name = "Al-Fatiha",
                ayahCount = 7,
                isMakkia = true
            ),
            Surah(
                id = 2,
                order = Surah.SurahOrder.AlBaqarah,
                name = "Al-Baqara",
                ayahCount = 286,
                isMakkia = false
            ),
            Surah(
                id = 3,
                order = Surah.SurahOrder.AalImran,
                name = "Aal-E-Imran",
                ayahCount = 200,
                isMakkia = false
            ),
            Surah(
                id = 4,
                order = Surah.SurahOrder.AnNisa,
                name = "An-Nisa",
                ayahCount = 176,
                isMakkia = false
            ),
            Surah(
                id = 5,
                order = Surah.SurahOrder.AlMaidah,
                name = "Al-Maidah",
                ayahCount = 120,
                isMakkia = true
            ),
            Surah(
                id = 6,
                order = Surah.SurahOrder.AlAnam,
                name = "Al-An'am",
                ayahCount = 165,
                isMakkia = true
            ),
            Surah(
                id = 7,
                order = Surah.SurahOrder.AlAraf,
                name = "Al-A'raf",
                ayahCount = 206,
                isMakkia = true
            ),
            Surah(
                id = 8,
                order = Surah.SurahOrder.AlAnfal,
                name = "Al-Anfal",
                ayahCount = 75,
                isMakkia = true
            ),
            Surah(
                id = 9,
                order = Surah.SurahOrder.AtTawbah,
                name = "At-Tawbah",
                ayahCount = 129,
                isMakkia = true
            ),
        )
    }
}
