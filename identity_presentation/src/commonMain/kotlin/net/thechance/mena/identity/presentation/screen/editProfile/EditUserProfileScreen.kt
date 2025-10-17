package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
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
import mena.identity_presentation.generated.resources.more_horizontal
import mena.identity_presentation.generated.resources.options
import mena.identity_presentation.generated.resources.save_changes
import mena.identity_presentation.generated.resources.username
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.base.BaseScreen
import net.thechance.mena.identity.presentation.components.WheelDatePicker
import net.thechance.mena.identity.presentation.screen.editProfile.component.AtPrefixTransformation
import net.thechance.mena.identity.presentation.screen.editProfile.component.GenderToggle
import net.thechance.mena.identity.presentation.screen.editProfile.component.ProfileEditText
import net.thechance.mena.identity.presentation.screen.editProfile.component.ProfileImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class EditUserProfileScreen : BaseScreen<
        EditUserProfileViewModel,
        EditUserProfileUIState,
        EditUserProfileUIEffect,
        EditUserProfileInteractionListener>() {
    @Composable
    override fun Content() {
        InitScreen(getScreenModel())
    }

    @Composable
    override fun OnRender(
        state: EditUserProfileUIState,
        listener: EditUserProfileInteractionListener,
    ) {
        val scrollState = rememberScrollState()
        LaunchedEffect(state.errorMessage) {
            delay(3000)
            listener.clearErrorMessage()
        }

        Scaffold(
            snakeBar = {
                AnimatedVisibility(
                    visible = state.errorMessage?.isNotEmpty() ?: false,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SnackBar(
                        title = stringResource(Res.string.error),
                        message = state.errorMessage.orEmpty(),
                        leadingIcon = painterResource(Res.drawable.ic_close_circle),
                        modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._16)
                            .padding(horizontal = Theme.spacing._16)
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
                            modifier = Modifier
                                .clip(RoundedCornerShape(Theme.radius.md))
                                .background(Theme.colorScheme.background.surfaceLow)
                                .clickable(onClick = { listener.onClickCancelButton() }),
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = stringResource(Res.string.back),
                        )
                    },
                    trailingContent = {
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Theme.radius.md))
                                .background(Theme.colorScheme.background.surfaceLow)
                                .clickable(
                                    onClick = {
                                        //todo open logout dialog
                                    },
                                )
                                .padding(10.dp)
                                .size(20.dp),
                            painter = painterResource(Res.drawable.more_horizontal),
                            contentDescription = stringResource(Res.string.options),
                        )
                    }
                )

                ProfileImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    profilePicture = state.profileImageUrl,
                )

                ProfileEditText(
                    title = stringResource(Res.string.first_name),
                    value = state.firstName,
                    onValueChange = { listener.onChangeFirstName(it) },
                )

                ProfileEditText(
                    title = stringResource(Res.string.last_name),
                    value = state.lastName,
                    onValueChange = { listener.onChangeLastName(it) },
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

                WheelDatePicker(
                    modifier = Modifier.padding(top = Theme.spacing._16),
                    selectedDate = state.birthDate,
                    onDateChange = { day, month, year ->
                        listener.onChangeDate(day, month, year)
                    },
                )

                GenderToggle(
                    gender = state.gender,
                    onGenderChange = { listener.onChangeGender(it) }
                )

                PrimaryButton(
                    modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._24),
                    text = stringResource(Res.string.save_changes),
                    onClick = { listener.onClickSaveButton() }
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._8),
                    text = stringResource(Res.string.cancel),
                    onClick = { listener.onClickCancelButton() }
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
        }
    }
}
