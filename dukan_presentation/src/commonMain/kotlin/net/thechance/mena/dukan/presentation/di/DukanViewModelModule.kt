package net.thechance.mena.dukan.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductViewModel
import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfViewModel
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropViewModel
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val dukanViewModelModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    viewModelOf(::CreateDukanViewModel)
    viewModelOf(::ManageDukanViewModel)
    viewModelOf(::ImageCropViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::ManageShelfViewModel)
    viewModelOf(::CreateShelfViewModel)
}