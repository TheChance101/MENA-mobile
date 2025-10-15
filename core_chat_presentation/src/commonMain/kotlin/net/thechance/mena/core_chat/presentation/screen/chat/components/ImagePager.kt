package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_cancel
import mena.core_chat_presentation.generated.resources.ic_download
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.presentation.components.CustomInfiniteCircularLoader
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.core_chat.presentation.utils.formatAsPastDateTime
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullImagePagerView(
    message: MessageUiState?,
    senderName: String,
    senderImageUrl: String,
    initialPage: Int,
    onCloseClick: () -> Unit,
    onDownloadClick: (url: String) -> Unit,
) {
    if (message == null || message.content !is MessageContent.Images) return
    val imagesSource = message.content.source
    if (imagesSource !is ImagesSource.Remote) return

    val images = imagesSource.urls

    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { images.size })
    Box(
        modifier = Modifier.fillMaxSize().background(Theme.colorScheme.background.surface)
    ) {
        HorizontalImagePager(
            state = pagerState,
            images = images,
        )

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

        PagerOverlay(
            senderName = senderName,
            senderImageUrl = senderImageUrl,
            time = message.sendTime,
            onDownloadClicked = { onDownloadClick(images[pagerState.currentPage]) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    BackHandler(
        enabled = true,
        onBack = onCloseClick
    )
}

@Composable
fun HorizontalImagePager(
    state: PagerState,
    images: List<String>,
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
            AsyncImage(
                model = images[page],
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

@Composable
private fun PagerOverlay(
    senderName: String,
    senderImageUrl: String,
    time: LocalDateTime,
    onDownloadClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgGradientColor = Brush.verticalGradient(
        colors = listOf(
            Theme.colorScheme.primary.primary.copy(alpha = 0f),
            Theme.colorScheme.primary.primary.copy(alpha = 0.44f)
        )
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
                text = time.formatAsPastDateTime(),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.primary.onPrimaryBody
            )
        }

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