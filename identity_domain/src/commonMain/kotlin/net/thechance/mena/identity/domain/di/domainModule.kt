package net.thechance.mena.identity.domain.di

import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.MobileNumberValidator
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::MobileNumberValidator)
    singleOf(::AuthorizationService)
    singleOf(::PasswordValidator)
}