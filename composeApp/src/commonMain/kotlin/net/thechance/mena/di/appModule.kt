package net.thechance.mena.di

import net.thechance.mena.AppEnvironment
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APP_VERSION = "appVersion"
val appModule = module {
    single(named(APP_VERSION)) { AppEnvironment.versionName }
}