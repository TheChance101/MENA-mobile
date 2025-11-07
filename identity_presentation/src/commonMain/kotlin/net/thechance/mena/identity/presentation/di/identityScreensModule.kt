package net.thechance.mena.identity.presentation.di

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddEditLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen.EnableLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.changePassword.ChangePasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.editProfile.EditUserProfileViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperComponentViewModel
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperUiState
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperViewModel
import net.thechance.mena.identity.presentation.screen.login.LoginScreenViewModel
import net.thechance.mena.identity.presentation.screen.notImplemented.NotImplementedScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.components.dialog.ShareDialogViewModel
import net.thechance.mena.identity.presentation.screen.register.accountCreated.AccountCreatedViewModel
import net.thechance.mena.identity.presentation.screen.register.createPassword.CreatePasswordViewModel
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.enterName.EnterNameViewModel
import net.thechance.mena.identity.presentation.screen.register.otp.RegisterOtpViewModel
import net.thechance.mena.identity.presentation.screen.register.phoneEntry.RegisterPhoneEntryViewModel
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.uploadProfileImage.UploadProfileImageViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.otp.ForgetPasswordOtpScreenViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry.ForgetPasswordPhoneEntryScreenViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.setNewPassword.SetNewPasswordScreenViewModel
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
const val GALLERY_IMAGES = "GALLERY_IMAGES"

val identityScreensModule = module {

    includes(platformModule())
    factory(named(LOCATION_FOREGROUND)) { PermissionHandler(get(named(LOCATION_FOREGROUND))) }
    factory(named(GALLERY_IMAGES)) { PermissionHandler(get(named(GALLERY_IMAGES))) }
    factory { ProfileScreenViewModel(get(), get(), get(named(APP_VERSION)), get()) }
    factoryOf(::ImageCropperViewModel)
    factoryOf(::LoginScreenViewModel)
    factoryOf(::NotImplementedScreenViewModel)
    factoryOf(::RegisterPhoneEntryViewModel)
    factoryOf(::RegisterOtpViewModel)
    factoryOf(::EnterNameViewModel)
    factoryOf(::CreatePasswordViewModel)
    factoryOf(::AccountCreatedViewModel)
    factoryOf(::ForgetPasswordPhoneEntryScreenViewModel)
    factoryOf(::ForgetPasswordOtpScreenViewModel)
    factoryOf(::EditUserProfileViewModel)
    factory { (authTokens: AuthenticationTokens?, phoneNumber: PhoneNumber?) ->
        UploadProfileImageViewModel(
            cachedImageRepository = get(),
            userRepository = get(),
            imageDecoder = get(),
            authenticationRepository = get(),
            registrationDraftRepository = get(),
            authTokens = authTokens,
            phoneNumber = phoneNumber
        )
    }
    factoryOf(::SetNewPasswordScreenViewModel)
    factoryOf(::AddressesScreenViewModel)
    factoryOf(::EnableLocationScreenViewModel)
    factoryOf(::ShareDialogViewModel)
    factory {
        DatePickerScreenViewModel(
            ageValidator = get(),
            registrationDraftRepository = get(),
            phoneNumber = it[0] as PhoneNumber,
            firstName = it[1] as String,
            lastName = it[2] as String,
            username = it[3] as String,
            password = it[4] as String
        )
    }
    factory {
        SelectGenderScreenViewModel(
            registerRepository = get(),
            registrationDraftRepository = get(),
            authenticationRepository = get(),
            phoneNumber = it[0] as PhoneNumber,
            firstName = it[1] as String,
            lastName = it[2] as String,
            username = it[3] as String,
            password = it[4] as String,
            birthDate = it[5] as LocalDate
        )
    }
    factoryOf(::ChangePasswordScreenViewModel)
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