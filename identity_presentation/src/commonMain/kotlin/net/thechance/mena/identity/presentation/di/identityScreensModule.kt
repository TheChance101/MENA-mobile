package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.core.util.factoryOfOrNull
import net.thechance.mena.identity.presentation.core.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.feature.authenticationFlow.login.LoginScreenViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.accountCreated.AccountCreatedViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.createPassword.CreatePasswordViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.datePicker.DatePickerScreenViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.enterName.EnterNameViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.otp.RegisterOtpViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.phoneEntry.RegisterPhoneEntryViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.selectGender.SelectGenderScreenViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.uploadProfileImage.UploadProfileImageViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.otp.ResetPasswordOtpScreenViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.phoneEntry.ResetPasswordPhoneEntryScreenViewModel
import net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.setNewPassword.SetNewPasswordScreenViewModel
import net.thechance.mena.identity.presentation.feature.locationFlow.addEditLocation.AddEditLocationScreenViewModel
import net.thechance.mena.identity.presentation.feature.locationFlow.enableLocationScreen.EnableLocationScreenViewModel
import net.thechance.mena.identity.presentation.feature.locationFlow.myAddresses.AddressesScreenViewModel
import net.thechance.mena.identity.presentation.feature.locationFlow.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.changePassword.ChangePasswordScreenViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.contactUs.ContactUsViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.editProfile.EditUserProfileViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.imageCropper.ImageCropperViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.imageCropper.components.imageCropper.ImageCropperComponentViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.imageCropper.components.imageCropper.ImageCropperUiState
import net.thechance.mena.identity.presentation.feature.profileFlow.privacyAndPolicy.PrivacyAndPolicyScreenViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.profile.ProfileScreenViewModel
import net.thechance.mena.identity.presentation.feature.profileFlow.profile.components.dialog.ShareDialogViewModel
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import net.thechance.mena.identity.presentation.utils.ImageDecoderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

const val APP_VERSION = "appVersion"
const val LOCATION_FOREGROUND = "LOCATION_FOREGROUND"
const val GALLERY_IMAGES = "GALLERY_IMAGES"

val identityScreensModule = module {

    includes(platformModule())
    factory(named(LOCATION_FOREGROUND)) { PermissionHandler(get(named(LOCATION_FOREGROUND))) }
    factory(named(GALLERY_IMAGES)) { PermissionHandler(get(named(GALLERY_IMAGES))) }
    factory { ProfileScreenViewModel(get(), get(), get(named(APP_VERSION)), get()) }
    factoryOf(::ImageCropperViewModel)
    factoryOf(::LoginScreenViewModel)
    factoryOf(::CreatePasswordViewModel)
    factoryOf(::AccountCreatedViewModel)
    factoryOf(::ResetPasswordPhoneEntryScreenViewModel)
    factoryOf(::ResetPasswordOtpScreenViewModel)
    factoryOf(::EditUserProfileViewModel)
    factoryOf(::SetNewPasswordScreenViewModel)
    factoryOf(::AddressesScreenViewModel)
    factoryOf(::EnableLocationScreenViewModel)
    factoryOf(::ShareDialogViewModel)
    factoryOf(::RegisterPhoneEntryViewModel)
    factoryOf(::RegisterOtpViewModel)
    factoryOf(::EnterNameViewModel)
    factoryOf(::UploadProfileImageViewModel)
    factoryOf(::DatePickerScreenViewModel)
    factoryOf(::SelectGenderScreenViewModel)
    factoryOf(::ChangePasswordScreenViewModel)
    factoryOf(::PrivacyAndPolicyScreenViewModel)
    factoryOf(::ContactUsViewModel)

    factoryOf(::ImageDecoderImpl) bind ImageDecoder::class
    viewModel { (minScale: Float, maxScale: Float, initialState: ImageCropperUiState) ->
        ImageCropperComponentViewModel(minScale, maxScale, initialState)
    }
    factoryOfOrNull(::AddEditLocationScreenViewModel)

    factory {
        PickLocationScreenViewModel(
            addressesRepository = get(),
            locationForegroundHandler = get(named(LOCATION_FOREGROUND)),
            addressModel = getOrNull()
        )
    }

    factory {
        EnableLocationScreenViewModel(
            locationForegroundHandler = get(named(LOCATION_FOREGROUND))
        )
    }

    factory {
        ShareDialogViewModel(
            userRepository = get(),
            imagesRepository = get(),
            galleryPermissionHandler = get(named(GALLERY_IMAGES)),
        )
    }
}