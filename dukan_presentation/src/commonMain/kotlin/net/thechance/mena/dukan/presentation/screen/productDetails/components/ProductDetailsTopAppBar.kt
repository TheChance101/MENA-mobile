package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.favorite_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_favorite_filled
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.shopping_basket_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewProductDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsAppBar(
    state: ProductDetailsUiState,
    listener: ProductDetailsInteractionListener
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._8)
    ) {
        AppBarIcon(
            painter = painterResource(Res.drawable.ic_arrow_left),
            contentDescription = stringResource(Res.string.back_arrow),
            onClick = listener::onBackClicked
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.padding(start = Theme.spacing._4),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        ) {
            AppBarIcon(
                painter = painterResource(
                    if (state.isFavorite) {
                        Res.drawable.ic_favorite_filled
                    } else {
                        Res.drawable.ic_favorite
                    }
                ),
                contentDescription = stringResource(Res.string.favorite_icon),
                tint = Color(state.dukanColor),
                onClick = listener::onToggleProductToFavoriteClicked
            )
            AppBarOptionContainer(
                isBadgeVisible = state.hasProductInCart,
                onClick = listener::onViewCartClicked
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_shopping_basket),
                    contentDescription = stringResource(Res.string.shopping_basket_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }
        }
    }
}

@Composable
private fun AppBarIcon(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Theme.colorScheme.primary.primary,
    modifier: Modifier = Modifier
) {
    AppBarOptionContainer(
        onClick = onClick
    ) {
        Icon(
            painter = painter,
            tint = tint,
            contentDescription = contentDescription,
            modifier = modifier.size(40.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun ProductDetailsAppBarPreview() {
    MenaTheme {
        ProductDetailsAppBar(
            state = ProductDetailsUiState(isFavorite = true),
            listener = PreviewProductDetailsInteractionListener
        )
    }
}