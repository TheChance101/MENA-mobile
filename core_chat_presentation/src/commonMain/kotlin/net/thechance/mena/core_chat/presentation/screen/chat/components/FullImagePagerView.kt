package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.am
import mena.core_chat_presentation.generated.resources.ic_cancel
import mena.core_chat_presentation.generated.resources.ic_download
import mena.core_chat_presentation.generated.resources.pm
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.presentation.components.CustomInfiniteCircularLoader
import net.thechance.mena.core_chat.presentation.screen.chat.ImageMessageUiState
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.core_chat.presentation.utils.formatAsPastDateTime
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalComposeUiApi::class, ExperimentalUuidApi::class)
@Composable
fun FullImagePagerView(
    messages: List<ImageMessageUiState>,
    senderName: String,
    senderImageUrl: String,
    initialPage: Int,
    isReactionDialogVisible: Boolean,
    currentUserId: Uuid?,
    onDismissReactionDialog: () -> Unit,
    onReactionSelected: (messageId: Uuid, emoji: String) -> Unit,
    onCloseClick: () -> Unit,
    onImageLongClick: (message: ImageMessageUiState) -> Unit,
    onDownloadClick: (String) -> Unit,
) {
    if (messages.isEmpty()) return onCloseClick()

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { messages.size }
    )
    val message = messages[pagerState.currentPage]
    val messageToReact = messages[pagerState.currentPage]
    Scaffold(
        modifier = Modifier.fillMaxSize().background(Theme.colorScheme.background.surface)
            .combinedClickable(
                onClick = {},
                onLongClick = { onImageLongClick(message) },
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ),
        fullScreen = true,
        overlays = {
            messageReactionDialog(
                isVisible = isReactionDialogVisible,
                onDismiss = onDismissReactionDialog,
                message = messageToReact,
                currentUserId = currentUserId,
                onReactionClicked = onReactionSelected
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            val images = messages.map { it.imageDate }
            HorizontalImagePager(state = pagerState, images = images)
            FabButton(
                painter = painterResource(Res.drawable.ic_cancel),
                shape = RoundedCornerShape(Theme.spacing._12),
                iconSize = 20.dp,
                contentPadding = PaddingValues(10.dp),
                containerColor = Theme.colorScheme.background.surfaceLow,
                contentColor = Theme.colorScheme.primary.primary,
                onClick = onCloseClick,
                modifier = Modifier.padding(
                    vertical = Theme.spacing._8,
                    horizontal = Theme.spacing._16
                ).statusBarsPadding(),
            )
            val message = messages[pagerState.currentPage]
            val isUrlImage = message.imageDate is ImageData.ImageUrl

            PagerOverlay(
                senderName = senderName,
                senderImageUrl = senderImageUrl,
                time = message.messageDetails.sendTime,
                isDownloadButtonVisible = isUrlImage,
                message = message,
                onDownloadClicked = { if (isUrlImage) onDownloadClick((message.imageDate).url) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    BackHandler(
        enabled = true,
        onBack = onCloseClick
    )
}

@Composable
fun HorizontalImagePager(
    state: PagerState,
    images: List<ImageData>,
) {
    HorizontalPager(
        state = state,
        modifier = Modifier.fillMaxSize(),
    ) { page ->

        var showLoadingIndicator by remember { mutableStateOf(true) }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (images[page] is ImageData.ImageByteArray) {
                val byteArray = (images[page] as ImageData.ImageByteArray).byteArray
                val imageBitmap = byteArray.toImageBitmap()
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                showLoadingIndicator = false

            } else if (images[page] is ImageData.ImageUrl) {

                AsyncImage(
                    model = (images[page] as ImageData.ImageUrl).url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    onState = { state ->
                        showLoadingIndicator = (state is AsyncImagePainter.State.Loading)
                    }
                )
                if (showLoadingIndicator) {
                    CustomInfiniteCircularLoader()
                }
            }
        }
    }
}

@Composable
private fun PagerOverlay(
    senderName: String,
    senderImageUrl: String,
    time: LocalDateTime,
    message: ImageMessageUiState,
    onDownloadClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isDownloadButtonVisible: Boolean,
) {
    val bgGradientColor = Brush.verticalGradient(
        colors = listOf(
            Theme.colorScheme.primary.primary.copy(alpha = 0f),
            Theme.colorScheme.primary.primary.copy(alpha = 0.44f)
        )
    )

    val am = stringResource(Res.string.am)
    val pm = stringResource(Res.string.pm)

    val timeFormat = time.formatAsPastDateTime(
        am = am,
        pm = pm
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(bgGradientColor)
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(
            Theme.spacing._8,
            Alignment.CenterHorizontally
        ),
    ) {
        CircularAvatar(
            contactImageUri = senderImageUrl,
            size = 40.dp,
            modifier = Modifier
                .border(width = 0.5.dp, color = Theme.colorScheme.stroke, shape = CircleShape)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        ) {
            Text(
                text = senderName,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.primary.onPrimary
            )
            Text(
                text = timeFormat,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.primary.onPrimaryBody
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                Theme.spacing._8,
                Alignment.CenterHorizontally
            )
        ) {
            if (message.messageDetails.reactions.isNotEmpty()) {
                ReactionBubble(reactions = message.messageDetails.reactions)
            }
            if (isDownloadButtonVisible) {
                FabButton(
                    onClick = onDownloadClicked,
                    painter = painterResource(Res.drawable.ic_download),
                    iconSize = 32.dp,
                    containerColor = Color.Transparent,
                    contentColor = Theme.colorScheme.primary.onPrimary,
                    contentPadding = PaddingValues(0.dp)
                )
            }
        }
    }
}