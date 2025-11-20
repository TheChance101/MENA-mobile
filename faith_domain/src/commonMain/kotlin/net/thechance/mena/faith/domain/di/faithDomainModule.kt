package net.thechance.mena.faith.domain.di

import net.thechance.mena.faith.domain.service.PrayerTimeService
import net.thechance.mena.faith.domain.service.QuranService
import net.thechance.mena.faith.domain.usecase.CalculateDistanceUseCase
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.domain.usecase.SearchRecitersUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val faithDomainModule = module {
    singleOf(::QiblahBearingCalculatorUseCase)
    singleOf(::CalculateDistanceUseCase)
    singleOf(::QuranService)
    singleOf(::PrayerTimeService)
    singleOf(::SearchRecitersUseCase)
}