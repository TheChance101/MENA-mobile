@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansViewModel
import net.thechance.mena.dukan.presentation.viewModel.checkout.CheckoutViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductViewModel
import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfViewModel
import net.thechance.mena.dukan.presentation.viewModel.cropImage.ImageCropViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanLocation.DukanLocationViewModel
import net.thechance.mena.dukan.presentation.viewModel.editProduct.EditProductViewModel
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanViewModel
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfViewModel
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsViewModel
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsViewModel
import net.thechance.mena.dukan.presentation.viewModel.search.SearchViewModel
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

internal val dukanViewModelModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }


    viewModelOf(::CreateDukanViewModel)
    viewModelOf(::ManageDukanViewModel)
    viewModelOf(::ImageCropViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::EditProductViewModel)
    viewModelOf(::ManageShelfViewModel)
    viewModelOf(::CreateShelfViewModel)
    viewModelOf(::DukanDetailsViewModel)
    viewModelOf(::DukanCategoriesViewModel)
    viewModelOf(::CategoryDukansViewModel)
    viewModelOf(::ShelfDetailsViewModel)
    viewModelOf(::ProductDetailsViewModel)
    viewModelOf(::DukanCartViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::DukanLocationViewModel)
    viewModelOf(::OrderDetailsViewModel)
}