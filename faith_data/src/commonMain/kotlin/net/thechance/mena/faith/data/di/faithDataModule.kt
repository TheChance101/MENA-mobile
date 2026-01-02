package net.thechance.mena.faith.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.MobileGeocoder
import net.thechance.mena.faith.data.database.AyahDao
import net.thechance.mena.faith.data.database.QuranDatabase
import net.thechance.mena.faith.data.database.RecitersDao
import net.thechance.mena.faith.data.database.SurahAudioDao
import net.thechance.mena.faith.data.database.prayertimes.PrayerTimesDao
import net.thechance.mena.faith.data.datastore.TilawahDataStore
import net.thechance.mena.faith.data.datastore.TilawahDataStoreImpl
import net.thechance.mena.faith.data.datastore.createDataStore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val faithDataModule = module {
    single<AyahDao> { get<QuranDatabase>().getAyaDao() }
    single<SurahAudioDao> { get<QuranDatabase>().getSurahAudioDao() }
    single<RecitersDao> { get<QuranDatabase>().getRecitersDao() }
    single<PrayerTimesDao> { get<QuranDatabase>().getPrayerTimesDao() }
    single<DataStore<Preferences>> { createDataStore() }
    singleOf(::TilawahDataStoreImpl) bind TilawahDataStore::class
    single<Geocoder> { MobileGeocoder() }
    includes(platformModule())
    includes(networkModule)
    includes(repositoryModule)

}
