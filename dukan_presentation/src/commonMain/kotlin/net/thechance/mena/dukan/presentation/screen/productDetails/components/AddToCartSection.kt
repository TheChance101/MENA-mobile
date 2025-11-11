package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_to_cart
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.ProductQuantityButton
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.min

@Composable
fun AddToCartSection(
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    productQuantity: Int,
    isLoading: Boolean,
    productPrice: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .height(120.dp)
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
            inCartQuantity = productQuantity,
            backgroundColor = Theme.colorScheme.background.surfaceHigh,
            iconPadding = PaddingValues(Theme.spacing._8 + Theme.spacing._2)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onAddToCartClick,
            isEnabled = true,
            isLoading = isLoading,
            loadingColors = listOf(
                Theme.colorScheme.stroke,
                Theme.colorScheme.shadeTertiary,
                Theme.colorScheme.primary.primary
            ),
            shape = RoundedCornerShape(Theme.radius.md),
            containerColor = Theme.colorScheme.primary.primary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        .clip(RoundedCornerShape(Theme.radius.full))
                        .background(Theme.colorScheme.primary.onPrimaryBody)
                )
                Column {
                    Text(
                        text = "$$productPrice",
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.primary.onPrimary,
                    )
                    Text(
                        text = "$$productPrice",
                        style = Theme.typography.label.small.copy(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        color = Theme.colorScheme.primary.onPrimaryBody,
                    )
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
            productQuantity = 1,
            onAddToCartClick = {},
            productPrice = 10.0,
            isLoading = false
        )
    }
}