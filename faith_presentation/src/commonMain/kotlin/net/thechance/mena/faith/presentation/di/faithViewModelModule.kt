package net.thechance.mena.faith.presentation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.presentation.feature.main.MainViewModel
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkViewModel
import net.thechance.mena.faith.presentation.feature.quran.qiblah.calibratedevice.CalibrateDeviceViewModel
import net.thechance.mena.faith.presentation.feature.quran.search.SearchViewModel
import net.thechance.mena.faith.presentation.feature.quran.search.args.ISearchArgs
import net.thechance.mena.faith.presentation.feature.quran.search.args.SearchArgsImpl
import net.thechance.mena.faith.presentation.feature.quran.sur.SurViewModel
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahViewModel
import net.thechance.mena.faith.presentation.feature.quran.surah.args.ISurahArgs
import net.thechance.mena.faith.presentation.feature.quran.surah.args.SurahArgsImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val faithViewModelModule = module {

    factory<CoroutineDispatcher> { Dispatchers.IO }

    factoryOf(::SurahArgsImpl) bind ISurahArgs::class
    factoryOf(::SearchArgsImpl) bind ISearchArgs::class

    viewModelOf(::SurahViewModel)
    viewModelOf(::SurViewModel)
    viewModelOf(::BookmarkViewModel)
    viewModelOf(::CalibrateDeviceViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::MainViewModel)


}

