package net.thechance.mena.di

import net.thechance.mena.AppEnvironment
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val BASE_URL = "baseUrl"

val networkModule = module {
    single(named(BASE_URL)) { AppEnvironment.baseUrl }
}