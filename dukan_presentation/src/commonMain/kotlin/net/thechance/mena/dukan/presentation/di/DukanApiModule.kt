package net.thechance.mena.dukan.presentation.di

import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.dukan.presentation.api.DukanApiImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val dukanApiModule = module{
    factoryOf(::DukanApiImpl) bind DukanApi::class
}