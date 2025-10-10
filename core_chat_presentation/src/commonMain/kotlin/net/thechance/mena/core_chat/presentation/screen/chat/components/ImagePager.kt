package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_cancel
import mena.core_chat_presentation.generated.resources.ic_download
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImagePager(
    senderName: String,
    senderImageUrl: String,
    images: List<String>,
    initialPage: Int,
    time: LocalDateTime,
    onCloseClick: () -> Unit,
    onDownloadClicked: (url: String) -> Unit
) {

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
            containerColor = Theme.colorScheme.background.surfaceLow,
            contentColor = Theme.colorScheme.primary.primary,
            modifier = Modifier.padding(
                vertical = Theme.spacing._8,
                horizontal = Theme.spacing._16
            ),
            onClick = onCloseClick
        )

        PagerOverlay(
            senderName = senderName,
            senderImageUrl = senderImageUrl,
            time = time,
            onDownloadClicked = { onDownloadClicked(images[pagerState.currentPage]) },
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
        AsyncImage(
            model = images[page],
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
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
            .background(bgGradientColor),
        verticalAlignment = Alignment.CenterVertically,
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
                text = time.toString(),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.primary.onPrimaryBody
            )
        }

        FabButton(
            onClick = onDownloadClicked,
            painter = painterResource(Res.drawable.ic_download),
            containerColor = Color.Transparent,
            contentColor = Theme.colorScheme.primary.onPrimary,
        )
    }
}