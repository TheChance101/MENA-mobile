package net.thechance.mena.faith.presentation.di

import net.thechance.mena.faith.presentation.feature.quran.sur.SurViewModel
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val faithViewModelModule = module {
    viewModel {
        SurViewModel(
            quranRepository = get()
        )
    }
    viewModel { (surahId: Int, surahName: String) ->
        SurahViewModel(
            quranRepository = get(),
            surahId = surahId,
            surahName = surahName,
            clipboardManager = get()
        )
    }
}