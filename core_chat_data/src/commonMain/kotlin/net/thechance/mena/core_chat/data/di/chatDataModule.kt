package net.thechance.mena.core_chat.data.di

import net.thechance.mena.faith.domain.di.faithDomainModule
import org.koin.dsl.module

val chatDataModule = module {
    includes(networkModule, repositoryModule, localDataModule, fileManagerModule, faithDomainModule)
}