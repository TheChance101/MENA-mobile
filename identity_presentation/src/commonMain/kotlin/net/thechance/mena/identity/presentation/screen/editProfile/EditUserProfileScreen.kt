package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.back
import mena.identity_presentation.generated.resources.cancel
import mena.identity_presentation.generated.resources.date_of_birth
import mena.identity_presentation.generated.resources.edit_profile_information
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.first_name
import mena.identity_presentation.generated.resources.ic_arrow_left
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.last_name
import mena.identity_presentation.generated.resources.save_changes
import mena.identity_presentation.generated.resources.username
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.GregorianDatePicker
import net.thechance.mena.identity.presentation.screen.editProfile.components.AtPrefixTransformation
import net.thechance.mena.identity.presentation.screen.editProfile.components.EditProfileImage
import net.thechance.mena.identity.presentation.screen.editProfile.components.GenderToggle
import net.thechance.mena.identity.presentation.screen.editProfile.components.MoreActionsButton
import net.thechance.mena.identity.presentation.screen.editProfile.components.ProfileEditText
import net.thechance.mena.identity.presentation.screen.editProfile.dialog.GetImageDialog
import net.thechance.mena.identity.presentation.screen.imageCropper.ImageCropperScreen
import net.thechance.mena.identity.presentation.util.rememberCameraPicker
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class EditUserProfileScreen : BaseScreen<
        EditUserProfileViewModel,
        EditUserProfileUIState,
        EditUserProfileUIEffect,
        EditUserProfileInteractionListener>() {
    @Composable
    override fun Content() {
        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) { factory.createPermissionsController() }
        InitScreen(getScreenModel(parameters = { parametersOf(controller) }))
        BindEffect(controller)
    }

    @Composable
    override fun OnRender(
        state: EditUserProfileUIState,
        listener: EditUserProfileInteractionListener,
    ) {
        val scrollState = rememberScrollState()

        val scope = rememberCoroutineScope()
        val galleryPicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
            file?.let { image ->
                scope.launch {
                    listener.onRequireCropImage(imageBitmap = image.toImageBitmap())
                }
            }
        }

        val cameraImagePicker = rememberCameraPicker { imageBitmap ->
            listener.onRequireCropImage(imageBitmap)
        }

        LaunchedEffect(state.showCamera) {
            if (state.showCamera) {
                cameraImagePicker.launch()
            }
            listener.onOpenCamera()
        }

        LaunchedEffect(state.errorMessage) {
            delay(3000)
            listener.clearErrorMessage()
        }

        Scaffold(
            snakeBar = {
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SnackBar(
                        title = stringResource(Res.string.error),
                        message = state.errorMessage?.let { stringResource(it) } ?: "",
                        leadingIcon = painterResource(Res.drawable.ic_close_circle),
                        modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._16)
                            .padding(horizontal = Theme.spacing._16)
                    )
                }
            },
            overlays = {
                dialog(state.showEditImageDialog) {
                    GetImageDialog(
                        isVisible = it,
                        onDismiss = listener::onDismissEditImageDialog,
                        onUploadImage = galleryPicker::launch,
                        onTakeImageFromCamera = listener::onTakeImageFromCamera,
                        onRemoveImage = listener::onRemoveProfileImage,
                    )
                }

                dialog(state.showLogoutDialog) {
                    Dialog(
                        isVisible = it,
                        title = "HI",
                        message = "Not Yet Implemented",
                        onDismiss = listener::onDismissLogoutDialog,
                        actionButtons = {}
                    )
                }
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(scrollState)
                    .background(Theme.colorScheme.background.surface)
                    .padding(horizontal = Theme.spacing._16)
                    .padding(bottom = Theme.spacing._16),
            ) {
                AppBar(
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 14.dp),
                    title = stringResource(Res.string.edit_profile_information),
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = stringResource(Res.string.back),
                        )
                    },
                    onLeadingClick = listener::onClickCancelButton,
                    trailingContent = {
                        MoreActionsButton(onClick = listener::onClickShowLogoutOptions)
                    }
                )

                EditProfileImage(
                    profileImageUrl = state.profileImageUrl,
                    profileImageBitmap = state.profileImageBitmap,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                    onEditClicked = listener::onClickEditImage,
                )

                ProfileEditText(
                    title = stringResource(Res.string.first_name),
                    value = state.firstName,
                    onValueChange = listener::onChangeFirstName,
                )

                ProfileEditText(
                    title = stringResource(Res.string.last_name),
                    value = state.lastName,
                    onValueChange = listener::onChangeLastName,
                )

                ProfileEditText(
                    title = stringResource(Res.string.username),
                    value = state.username,
                    onValueChange = { username ->
                        listener.onChangeUsername(
                            username = username.filter { it.isLetterOrDigit() || it == '_' }
                        )
                    },
                    visualTransformation = AtPrefixTransformation,
                )

                Text(
                    modifier = Modifier.padding(top = Theme.spacing._16),
                    text = stringResource(Res.string.date_of_birth),
                    style = Theme.typography.title.small
                )

                GregorianDatePicker(
                    modifier = Modifier.padding(top = Theme.spacing._16),
                    selectedDate = state.birthDate,
                    onDateChange = listener::onChangeDate,
                )

                GenderToggle(
                    gender = state.gender,
                    onChangeGender = listener::onChangeGender
                )

                PrimaryButton(
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._24),
                    text = stringResource(Res.string.save_changes),
                    onClick = listener::onClickSaveButton,
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._8),
                    text = stringResource(Res.string.cancel),
                    onClick = listener::onClickCancelButton
                )
            }
        }
    }

    override fun onEffect(
        effect: EditUserProfileUIEffect,
        navigator: Navigator,
    ) {
        when (effect) {
            EditUserProfileUIEffect.NavigateBackToProfile -> navigator.pop()
            is EditUserProfileUIEffect.NavigateToCropScreen -> {
                val cropperScreen = ImageCropperScreen(
                    imageKey = effect.imageKey,
                    onResult = effect.onResult,
                )
                navigator.push(cropperScreen)
            }
        }
    }
}
