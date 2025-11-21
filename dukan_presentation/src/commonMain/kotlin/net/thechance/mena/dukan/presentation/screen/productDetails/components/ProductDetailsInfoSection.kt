package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.productDetails.components.util.ShimmerBox
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProductDetails
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductDetailsInfoSection(
    state: ProductDetailsUiState.ProductInfo,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = Theme.spacing._16)) {
        if (isLoading) {
            ProductDetailsInfoShimmer()
        } else {
            ProductDetailsInfoContent(state)
        }
    }
}

@Composable
private fun ProductDetailsInfoShimmer() {
    ShimmerBox(
        width = 200.dp,
        height = Theme.typography.title.medium.fontSize.value.dp
    )
    Spacer(modifier = Modifier.height(Theme.spacing._8))
    ShimmerBox(
        width = 100.dp,
        height = Theme.typography.label.large.fontSize.value.dp
    )

    Spacer(modifier = Modifier.height(Theme.spacing._16))
    repeat(3) {
        ShimmerBox(
            width = if (it == 2) 150.dp else Dp.Unspecified,
            height = Theme.typography.body.small.fontSize.value.dp,
            modifier = Modifier.fillMaxWidth(if (it == 2) 0.7f else 1f)
        )
        Spacer(modifier = Modifier.height(Theme.spacing._4))
    }
}

@Composable
private fun ProductDetailsInfoContent(
    state: ProductDetailsUiState.ProductInfo
) {
    Text(
        text = state.name,
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary,
        textAlign = TextAlign.Start,
        maxLines = 2,
    )
    ProductDetailsPriceRow(
        price = state.price.toString(),
        modifier = Modifier.padding(top = Theme.spacing._2)
    )
    Text(
        text = state.description,
        style = Theme.typography.body.small,
        color = Theme.colorScheme.shadeSecondary,
        textAlign = TextAlign.Start,
        maxLines = 5,
        modifier = Modifier.padding(top = Theme.spacing._8, bottom = Theme.spacing._8)
    )
}

@Composable
private fun ProductDetailsPriceRow(
    price: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.discount_icon),
            contentDescription = stringResource(Res.string.discount_icon),
            tint = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(end = Theme.spacing._4)
        )
        Text(
            text = price,
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = Theme.spacing._4)
        )
        Image(
            painter = painterResource(Res.drawable.silver_tc),
            contentDescription = stringResource(Res.string.koin_icon),
            modifier = Modifier.size(24.dp)
        )
    }
}


@Preview
@Composable
private fun ProductDetailsInfoSectionPreview() {
    MenaTheme {
        ProductDetailsInfoSection(
            state = fakeProductDetails.product,
            isLoading = false
        )
    }
}

@Preview
@Composable
private fun ProductDetailsInfoSectionLoadingPreview() {
    MenaTheme {
        ProductDetailsInfoSection(
            state = fakeProductDetails.product,
            isLoading = true
        )
    }
}