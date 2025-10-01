package net.thechance.mena

import net.thechance.mena.di.apiModule
import net.thechance.mena.di.appModule
import net.thechance.mena.di.featureModule
import net.thechance.mena.di.networkModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)

        modules(
            modules = appModule + apiModule + featureModule + networkModule
        )
    }
}