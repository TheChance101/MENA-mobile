package net.thechance.mena.admin_panel.di

import net.thechance.mena.admin_panel.AppEnvironment
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val BASE_URL = "baseUrl"

val networkModule = module {
    single(named(BASE_URL)) { AppEnvironment.baseUrl }
}