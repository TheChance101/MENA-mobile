package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileViewModel
import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.screen.addresses.AddEditLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.AddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.forgetPassword.ForgetPasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.forgetPasswordOtp.OtpScreenViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperViewModel
import net.thechance.mena.identity.presentation.screen.login.LoginScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.RegisterScreenModel
import net.thechance.mena.identity.presentation.screen.resetPassword.ResetPasswordScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APP_VERSION = "appVersion"

val identityScreensModule = module {

    single { get<String>(named(APP_VERSION)) }
    factoryOf(::LoginScreenViewModel)
    factoryOf(::RegisterScreenModel)
    factoryOf(::ForgetPasswordScreenViewModel)
    factoryOf(::OtpScreenViewModel)
    factoryOf(::ProfileScreenViewModel)
    factoryOf(::EditUserProfileViewModel)
    factoryOf(::ResetPasswordScreenViewModel)
    factoryOf(::AddEditLocationScreenViewModel)
    factory { (imageBitmap: ImageBitmap) -> ImageCropperViewModel(imageBitmap) }
    factoryOf(::AddressesScreenViewModel)
}