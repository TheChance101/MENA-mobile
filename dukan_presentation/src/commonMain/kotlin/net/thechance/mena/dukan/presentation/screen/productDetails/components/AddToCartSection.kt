package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_to_cart
import mena.dukan_presentation.generated.resources.sold_out
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.ProductQuantityButton
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun AddToCartSection(
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    state: ProductDetailsUiState,
    modifier: Modifier = Modifier
) {
    if (state.product.isOutOfStock) {
        PrimaryButton(
            text = stringResource(Res.string.sold_out),
            onClick = {},
            modifier = Modifier
                .padding(
                    top = Theme.spacing._8,
                    bottom = Theme.spacing._16,
                    start = Theme.spacing._16,
                    end = Theme.spacing._16
                )
                .fillMaxWidth(),
            isEnabled = false,
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    ambientColor = Theme.colorScheme.primary.primary.copy(alpha = 0.4f),
                    spotColor = Theme.colorScheme.primary.primary.copy(alpha = 0.4f)
                )
                .background(Theme.colorScheme.background.surface)
                .padding(
                    top = Theme.spacing._8,
                    bottom = Theme.spacing._16,
                    start = Theme.spacing._16,
                    end = Theme.spacing._16
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
        ) {
            ProductQuantityButton(
                onPlusClick = onPlusClick,
                onMinusClick = onMinusClick,
                dukanColor = Color(state.dukanColor),
                inCartQuantity = state.product.inCartQuantity,
                backgroundColor = Theme.colorScheme.background.surfaceHigh,
                iconPadding = PaddingValues(Theme.spacing._8 + Theme.spacing._2)
            )
            Button(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .fillMaxWidth(),
                onClick = onAddToCartClick,
                isEnabled = state.isButtonEnable,
                isLoading = state.isAddToCartLoading,
                loadingColors = listOf(
                    Theme.colorScheme.stroke,
                    Theme.colorScheme.shadeTertiary,
                    Theme.colorScheme.primary.primary
                ),
                shape = SquircleShape(Theme.radius.md),
                containerColor = Color(state.dukanColor),
                disabledContainerColor = Theme.colorScheme.disabled,
                disabledContentColor = Theme.colorScheme.textDisabled,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.add_to_cart),
                        style = Theme.typography.label.medium,
                        color = Theme.colorScheme.primary.onPrimary,
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = Theme.spacing._8)
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(Theme.colorScheme.primary.onPrimaryBody)
                    )
                    Column {
                        Text(
                            text = "${state.product.finalPrice}$",
                            style = Theme.typography.label.small,
                            color = Theme.colorScheme.primary.onPrimary,
                        )
                        if (state.product.finalPrice < state.product.basePrice) {
                            Text(
                                text = "${state.product.basePrice}$",
                                style = Theme.typography.label.extraSmall.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = Theme.colorScheme.primary.onPrimary.copy(alpha = 0.6f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AddToCartSectionPreview() {
    MenaTheme {
        AddToCartSection(
            onPlusClick = {},
            onMinusClick = {},
            onAddToCartClick = {},
            state = ProductDetailsUiState(),
        )
    }
}