package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.coroutines.launch
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.button_skip_for_now
import mena.identity_presentation.generated.resources.button_upload
import mena.identity_presentation.generated.resources.desc_upload_profile_image
import mena.identity_presentation.generated.resources.title_complete_your_profile
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.AuthScreenContainer
import net.thechance.mena.identity.presentation.components.PageDescription
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.screen.profile.imageCropper.ImageCropperScreen
import net.thechance.mena.identity.presentation.screen.register.accountCreated.AccountCreatedScreen
import net.thechance.mena.identity.presentation.screen.register.shared.convertJsonStringToAuthUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toAuthUIStateJsonString
import net.thechance.mena.identity.presentation.screen.register.uploadProfileImage.components.UploadImageContainer
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

data class UploadProfileImageScreen(
    val authTokensUiStateJsonString: String
) : BaseScreen<
    UploadProfileImageViewModel,
    UploadProfileImageUIState,
    UploadProfileImageUIEffect,
    UploadProfileImageInteractionListener>() {

    @Composable
    override fun Content() {
        InitScreen(getScreenModel(parameters = { parametersOf(convertJsonStringToAuthUIState(authTokensUiStateJsonString)) }))
    }

    @Composable
    override fun OnRender(
        state: UploadProfileImageUIState,
        listener: UploadProfileImageInteractionListener
    ) {
        val scope = rememberCoroutineScope()
        val galleryPicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
            file?.let { image ->
                scope.launch { listener.onClickEdit(image.toImageBitmap()) }
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {
            AuthScreenContainer(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                PageDescription(
                    title = stringResource(Res.string.title_complete_your_profile),
                    subtitle = stringResource(Res.string.desc_upload_profile_image),
                )

                UploadImageContainer(
                    onClick = { galleryPicker.launch() },
                    image = state.imageBitmap,
                    modifier = Modifier
                        .width(328.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = stringResource(Res.string.button_upload),
                    onClick = listener::onClickUpload,
                    isEnabled = state.isUploadEnabled,
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._12)
                )

                TextButton(
                    text = stringResource(Res.string.button_skip_for_now),
                    onClick = listener::onClickSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }

    override fun onEffect(
        effect: UploadProfileImageUIEffect,
        navigator: Navigator,
        snackBarController: IdentitySnackBarController
    ) {
        when (effect) {
            is UploadProfileImageUIEffect.NavigateToAccountCreated -> {
                navigator.push(AccountCreatedScreen(effect.authUiState.toAuthUIStateJsonString()))
            }

            is UploadProfileImageUIEffect.NavigateToCropScreen -> {
                navigator.push(
                    ImageCropperScreen(
                        imageKey = effect.imageKey,
                        onResult = effect.onResult,
                    )
                )
            }

            is UploadProfileImageUIEffect.ShowSnackBarError -> {
                snackBarController.showSnackBarError(
                    message = effect.errorStringResource
                )
            }
        }
    }
}