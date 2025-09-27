package net.thechance.mena

import net.thechance.mena.di.networkModule
import net.thechance.mena.di.screenModule
import net.thechance.mena.identity.data.di.IdentityPlatformModule
import net.thechance.mena.identity.data.di.identityDataModule
import net.thechance.mena.identity.domain.di.domainModule
import net.thechance.mena.identity.presentation.di.identityScreensModule
import net.thechance.mena.wallet.data.di.WalletDataModule
import net.thechance.mena.wallet.domain.di.WalletDomainModule
import net.thechance.mena.wallet.presentation.di.WalletPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        val appModules = listOf(
            screenModule,
            networkModule
        )
        val identityModules = listOf(
            identityScreensModule,
            domainModule,
            IdentityPlatformModule,
            identityDataModule,
        )
        val walletModule = listOf(
            WalletPresentationModule().module,
            WalletDomainModule().module,
            WalletDataModule().module,
        )

        modules(
            modules = appModules + identityModules + walletModule // todo  + chatModules + dukanModules
        )
    }
}