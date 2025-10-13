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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import net.thechance.mena.trends.presentation.shared.util.gradientShadow
import net.thechance.mena.trends.presentation.video_player.VideoPlayer
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
            UserReelEffect.NavigateToPublisherProfile -> navController.navigate(Route.ManageReels)
        }
    }

    UserReelScreenContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun UserReelScreenContent(
    state: UserReelState,
    listener: UserReelInteractionListener
) {
//    val reels = state.reels.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { reelss.count() },
    )

    var currentReel: UserReelUiState? by remember { mutableStateOf(UserReelUiState()) }

    Scaffold(
        overlays = {
            dialog(isVisible = state.isConfirmationDialogVisible) {
                Dialog(
                    title = stringResource(Res.string.delete_reel),
                    message = stringResource(Res.string.confirmation_message),
                    buttonText = stringResource(Res.string.delete),
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    isVisible = state.isConfirmationDialogVisible,
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
                    isVisible = state.isReelDeleted == true && state.error == null,
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
                    isVisible = state.error != null,
                    onDismiss = { listener.onDismissErrorDialog() },
                    onCancelClick = { listener.onDismissErrorDialog() },
                    dialogCornerShape = RoundedCornerShape(12.dp),
                    cancelBackgroundShape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(16.dp)
                )
            }
        }
    ) {

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { page -> reelss[page].id },
        )
        { page ->

            ReelPage(
                reel = reelss[page] ,
                shouldRender = pagerState.currentPage == page,
                state = state,
                listener = listener
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
private fun ReelPage(
    reel: UserReelUiState,
    shouldRender: Boolean,
    state: UserReelState,
    listener: UserReelInteractionListener,
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        TopAppBar(onBackClick = listener::onBackClick, modifier = Modifier.zIndex(5f))

        if (shouldRender) {
            VideoPlayer(
                url = reel.videoUrl,
                playWhenVisible = true,
            )
        }

        UsersReAct(
            viewCount = reel.viewsCount.toString(),
            likeCount = reel.likesCount.toString(),
            isCurrentUserOwner = reel.isCurrentUserOwner,
            onDeleteClick = listener::onDeleteClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = Theme.spacing._16, bottom = 140.dp)
        )

        PublisherInfo(
            userName = reel.username,
            timeOfPublish = reel.createdAt.orEmpty(),
            description = reel.description,
            avatar = reel.profileImage,
            modifier = Modifier.align(Alignment.BottomCenter),
            isDescriptionExpanded = state.isDescriptionExpanded,
            onDescriptionClick = listener::onDescriptionClick,
            onPublisherInfoClick = listener::onPublisherInfoClick
        )

        Box(modifier = Modifier.fillMaxWidth().height(height = 118.dp).gradientShadow())
    }
}


@Composable
private fun PublisherInfo(
    avatar: String,
    userName: String,
    timeOfPublish: String,
    isDescriptionExpanded: Boolean,
    onPublisherInfoClick: () -> Unit,
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
            modifier = Modifier.padding(top = Theme.spacing._8)
        ) {

            Image(
                painter = rememberAsyncImagePainter(avatar),
                modifier = Modifier.size(size = 40.dp).clip(shape = CircleShape)
                    .border(shape = CircleShape, width = 0.5.dp, color = Theme.colorScheme.stroke)
                    .clickable { onPublisherInfoClick() },
                contentScale = ContentScale.Crop,
                contentDescription = "avatar image"
            )

            Column(Modifier.padding(bottom = Theme.spacing._16)) {
                Text(
                    text = userName,
                    color = Theme.colorScheme.primary.onPrimary,
                    style = Theme.typography.label.medium,
                    modifier = Modifier.padding(vertical = Theme.spacing._2)
                        .clickable { onPublisherInfoClick() },
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
    isCurrentUserOwner: Boolean,
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

        if (isCurrentUserOwner) {
            ReActIcon(
                icon = painterResource(resource = Res.drawable.ic_delete),
                label = stringResource(Res.string.delete),
                onClick = { onDeleteClick() },
            )
        }
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
        UserReelScreenContent(
            state = UserReelState(),
            listener = object : UserReelInteractionListener {
                override fun onDescriptionClick(isCollapsed: Boolean) {}
                override fun onPublisherInfoClick() {}
                override fun onBackClick() {}
                override fun onDeleteClick() {}
                override fun onConfirmDeleteClick() {}
                override fun onDismissSuccessDialog() {}
                override fun onDismissErrorDialog() {}
                override fun onDismissConfirmationDialog() {}
            }
        )
    }
}

val reelss = listOf(
    UserReelUiState(
        id = "12",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        description = "Description is very useful",
        likesCount = 2,
        viewsCount = 4,
        createdAt = "25-11",
        isCurrentUserOwner = false,
        username = "Hend",
        profileImage = "https://static.vecteezy.com/system/resources/previews/042/332/066/original/person-photo-placeholder-woman-default-avatar-profile-icon-grey-photo-placeholder-female-no-photo-images-for-unfilled-user-profile-greyscale-illustration-for-social-media-free-vector.jpg"
    ),

    UserReelUiState(
        id = "13",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        description = "Description",
        likesCount = 5,
        viewsCount = 10,
        createdAt = "2-11",
        isCurrentUserOwner = false,
        username = "Ahmed",
        profileImage = "https://www.bing.com/ck/a?!&&p=5164f87f101a6e605c32022a4e013c24b699dc1bb0782c7c91d09e9b489e2c28JmltdHM9MTc2MDE0MDgwMA&ptn=3&ver=2&hsh=4&fclid=1434bab1-96f1-6582-15f3-ac84974a6473&u=a1L2ltYWdlcy9zZWFyY2g_cT1pbWFnZSUyMHBsYWNlaG9sZGVyJTIwcHJvZmlsZSZGT1JNPUlRRlJCQSZpZD0zODJFOTlERkM0MzI4REQzRDY4OUI4NTVBRUQxRTFFRTA1NjM3MEJE"
    ),
    UserReelUiState(
        id = "14",
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        description = "Watch This Reel , it's very Interesting",
        likesCount = 0,
        viewsCount = 1,
        createdAt = "25-10",
        isCurrentUserOwner = true,
        username = "Fatima",
        profileImage = "https://static.vecteezy.com/system/resources/previews/042/332/066/original/person-photo-placeholder-woman-default-avatar-profile-icon-grey-photo-placeholder-female-no-photo-images-for-unfilled-user-profile-greyscale-illustration-for-social-media-free-vector.jpg"
    )
)