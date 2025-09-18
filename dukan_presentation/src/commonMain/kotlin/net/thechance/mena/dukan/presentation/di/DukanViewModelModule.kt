package net.thechance.mena.dukan.presentation.di

import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val dukanViewModelModule = module {
    viewModelOf(::MainViewModel)
}