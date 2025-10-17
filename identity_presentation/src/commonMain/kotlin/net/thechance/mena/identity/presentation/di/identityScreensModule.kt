package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileViewModel
import androidx.compose.ui.graphics.ImageBitmap
import net.thechance.mena.identity.presentation.screen.addresses.AddEditLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.enableLocationScreen.EnableLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.AddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.forgetPassword.ForgetPasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.forgetPasswordOtp.OtpScreenViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperViewModel
import net.thechance.mena.identity.presentation.screen.login.LoginScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.RegisterScreenModel
import net.thechance.mena.identity.presentation.screen.resetPassword.ResetPasswordScreenViewModel
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APP_VERSION = "appVersion"
const val LOCATION_FOREGROUND = "LOCATION_FOREGROUND"

val identityScreensModule = module {

    includes(platformModule())
    single { get<String>(named(APP_VERSION)) }

    factory { PermissionHandler(get(named(LOCATION_FOREGROUND))) }

    factory {
        PickLocationScreenViewModel(
            locationForegroundHandler = get(),
            dispatcher = get(),
            mobileLocationRepository = get(),
            addressModel = getOrNull()
        )
    }

    factory {
        AddEditLocationScreenViewModel(
            addressesRepository = get(),
            dispatcher = get(),
            addressModel = getOrNull(),
        )
    }

    factory { (imageBitmap: ImageBitmap) -> ImageCropperViewModel(imageBitmap) }

    factoryOf(::LoginScreenViewModel)
    factoryOf(::RegisterScreenModel)
    factoryOf(::ForgetPasswordScreenViewModel)
    factoryOf(::OtpScreenViewModel)
    factoryOf(::ProfileScreenViewModel)
    factoryOf(::EditUserProfileViewModel)
    factoryOf(::ResetPasswordScreenViewModel)
    factory { (imageBitmap: ImageBitmap) -> ImageCropperViewModel(imageBitmap) }
    factoryOf(::AddressesScreenViewModel)
    factoryOf(::EnableLocationScreenViewModel)
}