package net.thechance.mena

import net.thechance.mena.di.appModule
import net.thechance.mena.di.networkModule
import net.thechance.mena.di.apiModule
import net.thechance.mena.di.featureModule
import net.thechance.mena.identity.data.di.IdentityPlatformModule
import net.thechance.mena.identity.data.di.identityDataModule
import net.thechance.mena.identity.domain.di.domainModule
import net.thechance.mena.identity.presentation.di.identityScreensModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        val appModules = listOf(
            appModule,
            screenModule,
            networkModule
        )
        val identityModules = listOf(
            identityScreensModule,
            domainModule,
            IdentityPlatformModule,
            identityDataModule,
        )

        modules(
            modules = apiModule + featureModule + networkModule
        )
    }
}