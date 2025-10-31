package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsInfoSection(
    state: ProductDetailsUiState.ProductInfo,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = state.name,
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(top = Theme.spacing._16)
        )
        Row(
            modifier = modifier.padding(top = Theme.spacing._2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.discount_icon),
                contentDescription = stringResource(Res.string.discount_icon),
                modifier = modifier.padding(end = Theme.spacing._4)
            )
            Text(
                text = state.price.toString(),
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(end = Theme.spacing._4)
            )
            Image(
                painter = painterResource(Res.drawable.silver_tc),
                contentDescription = stringResource(Res.string.koin_icon),
                modifier = modifier.size(24.dp)
            )
        }
        Text(
            text = state.description,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Start,
            modifier = modifier.padding(top = Theme.spacing._8)
        )
    }

}

@Preview
@Composable
private fun ProductDetailsInfoSectionPreview() {
    MenaTheme {
        ProductDetailsInfoSection(
            state = ProductDetailsUiState.ProductInfo(
                id = 1.toString(),
                name = "Product Name",
                price = 100.0,
                description = "This is a product description. It's a very good product. You should buy it.",
            )
        )
    }
}