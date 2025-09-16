package net.thechance.mena.di

import net.thechance.mena.faith.data.di.faithDataModule
import net.thechance.mena.faith.presentation.di.faithPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            modules = listOf(
                faithPresentationModule, faithDataModule
            )
        )
    }
}
