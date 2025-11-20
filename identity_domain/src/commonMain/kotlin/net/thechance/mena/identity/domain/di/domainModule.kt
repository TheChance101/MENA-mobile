package net.thechance.mena.identity.domain.di

import net.thechance.mena.identity.domain.service.AppThemeService
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.identity.domain.service.LocalizationService
import net.thechance.mena.identity.domain.service.LocationService
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.domain.useCase.validation.age.AgeValidator
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.MobileNumberValidator
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::MobileNumberValidator)
    singleOf(::AuthorizationService)
    singleOf(::LocationService)
    singleOf(::PasswordValidator)
    singleOf(::AgeValidator)
    singleOf(::LocalizationService)
    singleOf(::AppThemeService)
}