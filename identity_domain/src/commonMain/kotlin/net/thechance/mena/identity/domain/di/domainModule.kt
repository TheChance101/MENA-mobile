package net.thechance.mena.identity.domain.di

import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.MobileNumberValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::MobileNumberValidator)
}