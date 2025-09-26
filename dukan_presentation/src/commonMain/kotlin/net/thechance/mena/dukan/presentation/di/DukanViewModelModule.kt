package net.thechance.mena.dukan.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfViewModel
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropViewModel
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val dukanViewModelModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    viewModelOf(::CreateDukanViewModel)
    viewModelOf(::ApprovedDukanViewModel)
    viewModelOf(::ImageCropViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ManageShelfViewModel)
    viewModelOf(::CreateShelfViewModel)
}