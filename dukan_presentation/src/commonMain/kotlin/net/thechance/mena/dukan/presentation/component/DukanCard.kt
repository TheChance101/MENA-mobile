package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukanUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukanCard(
    dukan: DukanUiState,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(RoundedCornerShape(Theme.radius.lg))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = dukan.imageUrl,
            contentDescription = dukan.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        FavoriteIcon(
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Theme.spacing._8)
        )

        Text(
            text = dukan.name,
            color = Theme.colorScheme.primary.onPrimary,
            style = Theme.typography.title.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Theme.spacing._8)
        )
    }
}

@Preview
@Composable
private fun DukanCardFavoritePreview() {
    MenaTheme {
        DukanCard(
            dukan = DukanUiState(
                id = "dukan1",
                name = "Dukan",
                imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400"
            ),
            onClick = {},
            isFavorite = true,
            onFavoriteClick = {}
        )
    }
}
