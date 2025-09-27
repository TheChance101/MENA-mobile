package net.thechance.mena.di

import net.thechance.mena.api.ComposeAppApiImpl
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.presentation.api.ComposeAppApi
import net.thechance.mena.identity.presentation.api.IdentityFeatureApiImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val screenModule = module {
    singleOf(::ComposeAppApiImpl) bind ComposeAppApi::class
    singleOf(::IdentityFeatureApiImpl) bind IdentityFeatureApi::class
}