package net.thechance.mena.faith.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.presentation.base.snackbar.DefaultSnackbarHandlerImpl
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.DownloadedSurViewModel
import net.thechance.mena.faith.presentation.feature.main.MainViewModel
import net.thechance.mena.faith.presentation.feature.mosque.NearbyMosquesViewModel
import net.thechance.mena.faith.presentation.feature.mosque.create.CreateMosqueViewModel
import net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.MosqueImageCropViewModel
import net.thechance.mena.faith.presentation.feature.mosque.shared.SharedImageViewModel
import net.thechance.mena.faith.presentation.feature.mosque.uploadImageScreen.UploadImageViewModel
import net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeViewModel
import net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice.CalibrateDeviceViewModel
import net.thechance.mena.faith.presentation.feature.qiblah.compass.CompassViewModel
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkViewModel
import net.thechance.mena.faith.presentation.feature.quran.reciter.ReciterSearchViewModel
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.SearchViewModel
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.args.SearchArgs
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.args.SearchArgsImpl
import net.thechance.mena.faith.presentation.feature.quran.sur.SurViewModel
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahViewModel
import net.thechance.mena.faith.presentation.feature.quran.surah.args.SurahArgs
import net.thechance.mena.faith.presentation.feature.quran.surah.args.SurahArgsImpl
import net.thechance.mena.faith.presentation.feature.quran.tilwah.TilawahViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val faithViewModelModule = module {
    factory<CoroutineDispatcher> { Dispatchers.IO }
    factory<SnackbarHandler> { DefaultSnackbarHandlerImpl() }

    factoryOf(::SurahArgsImpl) bind SurahArgs::class
    factoryOf(::SearchArgsImpl) bind SearchArgs::class
    viewModelOf(::SurahViewModel)
    viewModelOf(::SurViewModel)
    viewModelOf(::BookmarkViewModel)
    viewModelOf(::CalibrateDeviceViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::CompassViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::PrayerTimeViewModel)
    viewModelOf(::NearbyMosquesViewModel)
    viewModelOf(::TilawahViewModel)
    viewModelOf(::DownloadedSurViewModel)
    viewModelOf(::ReciterSearchViewModel)
    viewModelOf(::CreateMosqueViewModel)
    viewModelOf(::MosqueImageCropViewModel)
    viewModelOf(::UploadImageViewModel)
    singleOf(::SharedImageViewModel)
}

