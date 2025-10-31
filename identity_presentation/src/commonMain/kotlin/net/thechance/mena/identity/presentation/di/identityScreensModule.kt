package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileViewModel
import net.thechance.mena.identity.presentation.screen.enableLocationScreen.EnableLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.forgetPassword.ForgetPasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.forgetPasswordOtp.OtpScreenViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperComponentViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperUiState
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperViewModel
import net.thechance.mena.identity.presentation.screen.login.LoginScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.RegisterScreenModel
import net.thechance.mena.identity.presentation.screen.resetPassword.ResetPasswordScreenViewModel
import net.thechance.mena.identity.presentation.util.factoryOfOrNull
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import net.thechance.mena.identity.presentation.utils.ImageDecoderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

const val APP_VERSION = "appVersion"
const val LOCATION_FOREGROUND = "LOCATION_FOREGROUND"

val identityScreensModule = module {

    includes(platformModule())
    factory { PermissionHandler(get(named(LOCATION_FOREGROUND))) }
    factory { ProfileScreenViewModel(get(), get(named(APP_VERSION))) }
    factoryOf(::ImageCropperViewModel)
    factoryOf(::LoginScreenViewModel)
    factoryOf(::RegisterScreenModel)
    factoryOf(::ForgetPasswordScreenViewModel)
    factoryOf(::OtpScreenViewModel)
    factoryOf(::EditUserProfileViewModel)
    factoryOf(::ResetPasswordScreenViewModel)
    factoryOf(::AddressesScreenViewModel)
    factoryOf(::EnableLocationScreenViewModel)
    factoryOf(::ImageDecoderImpl) bind ImageDecoder::class
    viewModel { (minScale: Float, maxScale: Float, initialState: ImageCropperUiState) ->
        ImageCropperComponentViewModel(minScale, maxScale, initialState)
    }
    factoryOfOrNull(::AddEditLocationScreenViewModel)
    factoryOfOrNull(::PickLocationScreenViewModel)
}