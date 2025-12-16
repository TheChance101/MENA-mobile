package net.thechance.mena.identity.presentation.di

import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.AddressOperationStrategyFactory
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.CreateAddressStrategy
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.LocationManagementViewModel
import net.thechance.mena.identity.presentation.screen.addresses.addEditLocation.UpdateAddressStrategy
import net.thechance.mena.identity.presentation.screen.addresses.enableLocationScreen.EnableLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.MyAddressesScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.changePassword.ChangePasswordScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.contactUs.ContactUsViewModel
import net.thechance.mena.identity.presentation.screen.profile.editProfile.EditUserProfileViewModel
import net.thechance.mena.identity.presentation.screen.profile.imageCropper.ImageCropperComponentViewModel
import net.thechance.mena.identity.presentation.screen.profile.imageCropper.ImageCropperUiState
import net.thechance.mena.identity.presentation.screen.profile.imageCropper.ImageCropperViewModel
import net.thechance.mena.identity.presentation.screen.login.LoginScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.privacyAndPolicy.PrivacyAndPolicyScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.ProfileScreenViewModel
import net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components.share.ShareDialogViewModel
import net.thechance.mena.identity.presentation.screen.register.accountCreated.AccountCreatedViewModel
import net.thechance.mena.identity.presentation.screen.register.createPassword.CreatePasswordViewModel
import net.thechance.mena.identity.presentation.screen.register.datePicker.DatePickerScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.enterName.EnterNameViewModel
import net.thechance.mena.identity.presentation.screen.register.otp.RegisterOtpViewModel
import net.thechance.mena.identity.presentation.screen.register.phoneEntry.RegisterPhoneEntryViewModel
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.uploadProfileImage.UploadProfileImageViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.otp.ResetPasswordOtpScreenViewModel
import net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry.ResetPasswordPhoneEntryScreenViewModel
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

val identityScreensModule = module {

    includes(platformModule())
    factoryOf(::PermissionHandler)
    factory { ProfileScreenViewModel(get(), get(), get(named(APP_VERSION)), get()) }
    factoryOf(::ImageCropperViewModel)
    factoryOf(::LoginScreenViewModel)
    factoryOf(::CreatePasswordViewModel)
    factoryOf(::AccountCreatedViewModel)
    factoryOf(::ResetPasswordPhoneEntryScreenViewModel)
    factoryOf(::ResetPasswordOtpScreenViewModel)
    factoryOf(::EditUserProfileViewModel)
    factoryOf(::SetNewPasswordScreenViewModel)
    factoryOf(::MyAddressesScreenViewModel)
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
    factoryOf(::AddressOperationStrategyFactory)
    factoryOf(::UpdateAddressStrategy)
    factoryOf(::CreateAddressStrategy)
    factoryOf(::ImageDecoderImpl) bind ImageDecoder::class
    viewModel { (minScale: Float, maxScale: Float, initialState: ImageCropperUiState) ->
        ImageCropperComponentViewModel(minScale, maxScale, initialState)
    }
    factoryOfOrNull(::LocationManagementViewModel)
    factoryOfOrNull(::PickLocationScreenViewModel)
}