package net.thechance.mena.dukan.presentation.di

import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val dukanViewModelModule = module {
    viewModelOf(::CreateDukanViewModel)
    viewModelOf(::ImageCropViewModel)
}