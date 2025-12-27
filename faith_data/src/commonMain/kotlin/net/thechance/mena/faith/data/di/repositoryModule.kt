package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.remote.network.DownloadSurahManagerImpl
import net.thechance.mena.faith.data.repository.BookmarkRepositoryImpl
import net.thechance.mena.faith.data.repository.MosqueRepositoryImpl
import net.thechance.mena.faith.data.repository.PrayerTimeRepositoryImpl
import net.thechance.mena.faith.data.repository.QuranRepositoryImpl
import net.thechance.mena.faith.data.repository.location.LocationRepositoryImpl
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import net.thechance.mena.faith.domain.repository.LocationRepository
import net.thechance.mena.faith.domain.repository.MosqueRepository
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.service.DownloadSurahManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::QuranRepositoryImpl) bind QuranRepository::class
    singleOf(::PrayerTimeRepositoryImpl) bind PrayerTimeRepository::class
    singleOf(::BookmarkRepositoryImpl) bind BookmarkRepository::class
    singleOf(::MosqueRepositoryImpl) bind MosqueRepository::class
    singleOf(::DownloadSurahManagerImpl) bind DownloadSurahManager::class
    singleOf(::LocationRepositoryImpl) bind LocationRepository::class
}