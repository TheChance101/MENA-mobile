package net.thechance.mena.dukan.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropViewModel
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val dukanViewModelModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    viewModelOf(::CreateDukanViewModel)
    viewModelOf(::ImageCropViewModel)
    viewModelOf(::MainViewModel)
}