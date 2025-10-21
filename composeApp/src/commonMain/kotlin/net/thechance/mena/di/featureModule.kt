package net.thechance.mena.di

import net.thechance.mena.core_chat.data.di.chatDataModule
import net.thechance.mena.core_chat.presentation.di.chatPresentationModule
import net.thechance.mena.dukan.data.di.dukanDataModule
import net.thechance.mena.dukan.domain.di.dukanDomainModule
import net.thechance.mena.dukan.presentation.di.dukanPresentationModule
import net.thechance.mena.faith.data.di.faithDataModule
import net.thechance.mena.faith.domain.di.faithDomainModule
import net.thechance.mena.faith.presentation.di.faithPresentationModule
import net.thechance.mena.identity.data.di.IdentityPlatformModule
import net.thechance.mena.identity.data.di.identityDataModule
import net.thechance.mena.identity.domain.di.domainModule
import net.thechance.mena.identity.presentation.di.identityScreensModule
import net.thechance.mena.trends.data.di.TrendDataModule
import net.thechance.mena.trends.domain.di.TrendDomainModule
import net.thechance.mena.trends.presentation.di.TrendPresentationModule
import net.thechance.mena.wallet.data.di.WalletDataModule
import net.thechance.mena.wallet.domain.di.WalletDomainModule
import net.thechance.mena.wallet.presentation.di.WalletPresentationModule
import org.koin.dsl.module
import org.koin.ksp.generated.module

val featureModule = module {
    includes(
        identityScreensModule,
        domainModule,
        IdentityPlatformModule,
        identityDataModule,

        dukanPresentationModule,
        dukanDomainModule,
        dukanDataModule,

        chatDataModule,
        chatDataModule,
        chatPresentationModule,


        faithPresentationModule,
        faithDataModule,
        faithDomainModule,
        WalletDataModule().module,
        WalletDomainModule().module,
        WalletPresentationModule().module,

        TrendDataModule().module,
        TrendDomainModule().module,
        TrendPresentationModule().module,
    )

}