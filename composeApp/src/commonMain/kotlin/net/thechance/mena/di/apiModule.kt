package net.thechance.mena.di

import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.core_chat.presentation.api.CoreChatApiImp
import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.dukan.presentation.DukanApiImpl
import net.thechance.mena.faith.api.FaithApi
import net.thechance.mena.faith.presentation.FaithApiImpl
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.presentation.api.IdentityFeatureApiImpl
import net.thechance.mena.trends.api.TrendsApi
import net.thechance.mena.trends.presentation.navigation.TrendsApiImpl
import net.thechance.mena.wallet.api.WalletApi
import net.thechance.mena.wallet.presentation.WalletApiImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val apiModule = module {
    singleOf(::IdentityFeatureApiImpl) bind IdentityFeatureApi::class
    singleOf(::DukanApiImpl) bind DukanApi::class
    singleOf(::CoreChatApiImp) bind CoreChatApi::class
    singleOf(::TrendsApiImpl) bind TrendsApi::class
    singleOf(::FaithApiImpl) bind FaithApi::class
    singleOf(::WalletApiImpl) bind WalletApi::class
}