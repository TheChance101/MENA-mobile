package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.screen.forgetPassword.ForgetPasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.forgetPasswordOtp.OtpScreenViewModel
import net.thechance.mena.identity.presentation.screen.login.LoginScreenModel
import net.thechance.mena.identity.presentation.screen.register.RegisterScreenModel
import net.thechance.mena.identity.presentation.screen.resetPassword.ResetPasswordScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val identityScreensModule = module {
    factoryOf(::LoginScreenModel)
    factoryOf(::RegisterScreenModel)
    factoryOf(::ForgetPasswordScreenViewModel)
    factoryOf(::OtpScreenViewModel)
    factoryOf(::ResetPasswordScreenViewModel)
}