package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.changePassword.ChangePasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileViewModel
import net.thechance.mena.identity.presentation.screen.enableLocationScreen.EnableLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperComponentViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperUiState
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperViewModel
import net.thechance.mena.identity.presentation.screen.login.LoginScreenViewModel
import net.thechance.mena.identity.presentation.screen.notImplemented.NotImplementedScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.createPassword.CreatePasswordViewModel
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.otp.RegisterOtpViewModel
import net.thechance.mena.identity.presentation.screen.register.phoneEntry.RegisterPhoneEntryViewModel
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreenViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.otp.ForgetPasswordOtpScreenViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry.ForgetPasswordPhoneEntryScreenViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword.SetNewPasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.uploadProfileImage.UploadProfileImageViewModel
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
    factory { ProfileScreenViewModel(get(), get(),get(named(APP_VERSION)),get()) }
    factoryOf(::ImageCropperViewModel)
    factoryOf(::LoginScreenViewModel)
    factoryOf(::NotImplementedScreenViewModel)
    factoryOf(::RegisterPhoneEntryViewModel)
    factoryOf(::RegisterOtpViewModel)
    factoryOf(::CreatePasswordViewModel)
    factoryOf(::ForgetPasswordPhoneEntryScreenViewModel)
    factoryOf(::ForgetPasswordOtpScreenViewModel)
    factoryOf(::EditUserProfileViewModel)
    factoryOf(::UploadProfileImageViewModel)
    factoryOf(::SetNewPasswordScreenViewModel)
    factoryOf(::AddressesScreenViewModel)
    factoryOf(::EnableLocationScreenViewModel)
    factoryOf(::DatePickerScreenViewModel)
    factoryOf(::SelectGenderScreenViewModel)
    factoryOf(::ChangePasswordScreenViewModel)
    factoryOf(::ImageDecoderImpl) bind ImageDecoder::class
    viewModel { (minScale: Float, maxScale: Float, initialState: ImageCropperUiState) ->
        ImageCropperComponentViewModel(minScale, maxScale, initialState)
    }
    factoryOfOrNull(::AddEditLocationScreenViewModel)
    factoryOfOrNull(::PickLocationScreenViewModel)
}