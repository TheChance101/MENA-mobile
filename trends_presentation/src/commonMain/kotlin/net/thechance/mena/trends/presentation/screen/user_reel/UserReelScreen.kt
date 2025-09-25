package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.confirmation_message
import mena.trends_presentation.generated.resources.delete
import mena.trends_presentation.generated.resources.delete_reel
import mena.trends_presentation.generated.resources.fail_delete_message
import mena.trends_presentation.generated.resources.fail_delete_title
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.ic_delete
import mena.trends_presentation.generated.resources.ic_eye
import mena.trends_presentation.generated.resources.ic_like
import mena.trends_presentation.generated.resources.react
import mena.trends_presentation.generated.resources.success_delete_message
import mena.trends_presentation.generated.resources.success_delete_title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import net.thechance.mena.trends.presentation.shared.util.gradientShadow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UserReelScreen(
    viewModel: UserReelViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            UserReelEffect.NavigateBack -> navController.popBackStack()
        }
    }

    UserReelScreenContent(
        state = state,
        listener = viewModel
    )
}

// TODO: That Screen Should be improved later when implementing video player
@Composable
private fun UserReelScreenContent(
    state: UserReelState,
    listener: UserReelInteractionListener
) {
    Scaffold(
        overlays = {
            dialog(isVisible = state.isConfirmationDialogVisible) {
                Dialog(
                    title = stringResource(Res.string.delete_reel),
                    message = stringResource(Res.string.confirmation_message),
                    buttonText = stringResource(Res.string.delete),
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    onDismiss = { listener.onDismissConfirmationDialog() },
                    onActionClick = { listener.onConfirmDeleteClick() },
                    onCancelClick = { listener.onDismissConfirmationDialog() },
                    dialogCornerShape = RoundedCornerShape(12.dp),
                    cancelBackgroundShape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(16.dp)
                )
            }

            dialog(state.isReelDeleted == true && state.error == null) {
                Dialog(
                    title = stringResource(Res.string.success_delete_title),
                    message = stringResource(Res.string.success_delete_message),
                    buttonText = "",
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    onDismiss = {
                        listener.onDismissSuccessDialog()
                        listener.onBackClick()
                    },
                    onCancelClick = {
                        listener.onDismissSuccessDialog()
                        listener.onBackClick()
                    },
                    dialogCornerShape = RoundedCornerShape(12.dp),
                    cancelBackgroundShape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(16.dp)
                )
            }

            dialog(state.error != null) {
                Dialog(
                    title = stringResource(Res.string.fail_delete_title),
                    message = stringResource(Res.string.fail_delete_message),
                    buttonText = "",
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    onDismiss = { listener.onDismissErrorDialog() },
                    onCancelClick = { listener.onDismissErrorDialog() },
                    dialogCornerShape = RoundedCornerShape(12.dp),
                    cancelBackgroundShape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(16.dp)
                )
            }
        },
        topBar = { TopAppBar(onBackClick = listener::onBackClick) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            UsersReAct(
                viewCount = state.viewsCount.toString(),
                likeCount = state.likesCount.toString(),
                onDeleteClick = listener::onDeleteClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Theme.spacing._16, bottom = 140.dp)
            )

            PublisherDetails(
                userName = state.username,
                timeOfPublish = state.createdAt,
                description = state.description,
                avatar = state.thumbnail,
                modifier = Modifier.align(Alignment.BottomCenter),
                isDescriptionExpanded = state.isDescriptionExpanded,
                onDescriptionClick = listener::onDescriptionClick
            )

            Box(
                modifier = Modifier.fillMaxWidth().height(height = 118.dp).gradientShadow()
            )
        }
    }
}

@Composable
private fun TopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppBar(
        title = "",
        modifier = modifier
            .fillMaxWidth()
            .gradientShadow()
            .padding(horizontal = Theme.spacing._16).padding(top = 8.dp),
        contentPadding = PaddingValues(0.dp),
        leadingContent = {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_arrow_left),
                contentDescription = "Back Icon",
            )
        },
        onLeadingClick = { onBackClick() }
    )
}

@Composable
private fun PublisherDetails(
    avatar: String,
    userName: String,
    timeOfPublish: String,
    isDescriptionExpanded: Boolean,
    onDescriptionClick: (Boolean) -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Theme.spacing._32)
            .padding(horizontal = Theme.spacing._16)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._8),
            modifier = Modifier
                .padding(top = Theme.spacing._8)
        ) {

            Image(
                painter = rememberAsyncImagePainter(avatar),
                modifier = Modifier.size(size = 40.dp).clip(shape = CircleShape)
                    .border(shape = CircleShape, width = 0.5.dp, color = Theme.colorScheme.stroke),
                contentScale = ContentScale.Crop,
                contentDescription = "avatar image"
            )

            Column(Modifier.padding(bottom = Theme.spacing._16)) {
                Text(
                    text = userName,
                    color = Theme.colorScheme.primary.onPrimary,
                    style = Theme.typography.label.medium,
                    modifier = Modifier.padding(vertical = Theme.spacing._2)
                )

                Text(
                    text = timeOfPublish,
                    color = Theme.colorScheme.shadeTertiary,
                    style = Theme.typography.label.small
                )
            }
        }

        if (description.isNotBlank()) {
            Text(
                text = description,
                modifier = Modifier
                    .animateContentSize()
                    .clickable { onDescriptionClick(isDescriptionExpanded) },
                color = Theme.colorScheme.primary.onPrimary,
                style = Theme.typography.label.medium,
                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 1
            )
        }
    }
}

@Composable
private fun UsersReAct(
    likeCount: String,
    viewCount: String,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._24)
    ) {
        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_like),
            label = likeCount
        )

        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_eye),
            label = viewCount
        )

        ReActIcon(
            icon = painterResource(resource = Res.drawable.ic_delete),
            label = stringResource(Res.string.delete),
            onClick = { onDeleteClick() },

            )
    }
}

@Composable
private fun ReActIcon(
    icon: Painter,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = icon,
            contentDescription = stringResource(Res.string.react),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable { onClick() },
            tint = Theme.colorScheme.shadeTertiary
        )
        Text(
            text = label,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeTertiary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        UserReelScreen()
    }
}