package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.image.MenaImage
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
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

    UserReelScreenContent(
        state = state,
        listener = viewModel
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                UserReelEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }
}

@Composable
private fun UserReelScreenContent(
    state: UserReelState,
    listener: UserReelInteractionListener
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Dialog(
            visible = state.isConfirmationDialogVisible,
            title = stringResource(Res.string.delete_reel),
            message = stringResource(Res.string.confirmation_message),
            buttonText = stringResource(Res.string.delete),
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            onDismiss = { listener.onDismissConfirmationDialog() },
            onActionClick = { listener.onConfirmDeleteClick() },
            onCancelClick = { listener.onDismissConfirmationDialog() },
            modifier = Modifier.align(Alignment.Center).zIndex(3f),
            dialogCornerShape = RoundedCornerShape(12.dp),
            cancelBackgroundShape = RoundedCornerShape(50),
            contentPadding = PaddingValues(16.dp)
        )

        Dialog(
            visible = state.isReelDeleted == true && state.error == null,
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
            modifier = Modifier.align(Alignment.Center).zIndex(3f),
            dialogCornerShape = RoundedCornerShape(12.dp),
            cancelBackgroundShape = RoundedCornerShape(50),
            contentPadding = PaddingValues(16.dp)
        )

        Dialog(
            visible = state.error != null,
            title = stringResource(Res.string.fail_delete_title),
            message = stringResource(Res.string.fail_delete_message),
            buttonText = "",
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            onDismiss = { listener.onDismissErrorDialog() },
            onCancelClick = { listener.onDismissErrorDialog() },
            modifier = Modifier.align(Alignment.Center).zIndex(3f),
            dialogCornerShape = RoundedCornerShape(12.dp),
            cancelBackgroundShape = RoundedCornerShape(50),
            contentPadding = PaddingValues(16.dp)
        )

        RunningVideoPlaceHolder()

        TopAppBar(onBackClick = listener::onBackClick)

        UsersReAct(
            viewCount = state.viewsCount.toString(),
            likeCount = state.likesCount.toString(),
            onDeleteClick = listener::onDeleteClick,
            modifier = Modifier.align(Alignment.BottomEnd)
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
                .align(Alignment.BottomCenter)
        )
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
            .height(height = 96.dp)
            .gradientShadow()
            .padding(horizontal = Theme.spacing._16).padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp
            )
            .requiredHeight(height = 56.dp),
        contentPadding = PaddingValues(0.dp),
        leadingContent = {
            MenaIcon(painter = painterResource(resource = Res.drawable.ic_arrow_left))
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

            MenaImage(
                painter = rememberAsyncImagePainter(avatar),
                modifier = Modifier.size(size = 40.dp).clip(shape = CircleShape)
                    .border(shape = CircleShape, width = 0.5.dp, color = Theme.colorScheme.stroke),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(bottom = Theme.spacing._16)) {
                MenaText(
                    text = userName,
                    color = Theme.colorScheme.primary.onPrimary,
                    style = Theme.typography.label.medium,
                    modifier = Modifier.padding(vertical = Theme.spacing._2)
                )

                MenaText(
                    text = timeOfPublish,
                    color = Theme.colorScheme.shadeTertiary,
                    style = Theme.typography.label.small
                )
            }
        }

        if (description.isNotBlank()) {
            MenaText(
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
private fun RunningVideoPlaceHolder() {
    Box(Modifier.background(Color.Black).fillMaxSize())
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
        MenaIcon(
            painter = icon,
            contentDescription = stringResource(Res.string.react),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable { onClick() },
            tint = Theme.colorScheme.shadeTertiary
        )
        MenaText(
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