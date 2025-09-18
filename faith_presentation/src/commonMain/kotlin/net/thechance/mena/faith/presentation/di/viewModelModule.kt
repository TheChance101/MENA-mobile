package net.thechance.mena.faith.presentation.di

import net.thechance.mena.faith.presentation.feature.quran.sur.SurViewModel
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModelOf(::SurViewModel)
    viewModel { (surahId: Int, surahName: String) ->
        SurahViewModel(
            get(), surahId, surahName
        )

    }
}