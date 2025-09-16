package net.thechance.mena.dukan.domain.di

import org.koin.dsl.module

val dukanDomainModule = module {
    includes(dukanUseCaseModule)
}