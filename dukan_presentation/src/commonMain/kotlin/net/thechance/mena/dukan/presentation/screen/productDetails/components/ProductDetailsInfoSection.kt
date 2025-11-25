package net.thechance.mena.dukan.presentation.screen.productDetails.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.discount_icon
import mena.dukan_presentation.generated.resources.koin_icon
import mena.dukan_presentation.generated.resources.see_less
import mena.dukan_presentation.generated.resources.see_more
import mena.dukan_presentation.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.AnnotatedText
import net.thechance.mena.dukan.presentation.screen.productDetails.components.util.ShimmerBox
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProductDetails
import net.thechance.mena.dukan.presentation.util.text.buildExpandableText
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
        price = state.finalPrice,
        discountPrice = state.basePrice,
        modifier = Modifier.padding(top = Theme.spacing._2)
    )
    ProductDescription(
        description = state.description
    )
}

@Composable
private fun ProductDetailsPriceRow(
    price: Double,
    discountPrice: Double,
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
            text = "$${discountPrice}",
            style = Theme.typography.label.extraSmall.copy(
                textDecoration = TextDecoration.LineThrough
            ),
            color = Theme.colorScheme.shadeTertiary,
            modifier = Modifier.padding(end = 2.dp)
        )
        Text(
            text = price.toString(),
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

@Composable
private fun ProductDescription(
    description: String,
    modifier: Modifier = Modifier
) {

    val isDescriptionExpanded = remember { mutableStateOf(false) }
    val isDescriptionClickable = remember { mutableStateOf(false) }
    val visibleDescription = remember { mutableStateOf("") }
    val seeLessText = stringResource(Res.string.see_less)
    val seeMoreText = stringResource(Res.string.see_more)
    val descriptionColor = Theme.colorScheme.shadeSecondary
    val seeLessAndMoreColor = Theme.colorScheme.primary.primary
    val maxLine = if (isDescriptionExpanded.value) Int.MAX_VALUE else 5
    val bringIntoViewRequester = BringIntoViewRequester()

    val displayedText = remember(isDescriptionExpanded.value, visibleDescription.value) {
        buildExpandableText(
            fullText = description,
            isTextExpanded = isDescriptionExpanded.value,
            visiblePartOfText = visibleDescription.value,
            textColor = descriptionColor,
            seeLessAndMoreColor = seeLessAndMoreColor,
            seeLessText = seeLessText,
            seeMoreText = seeMoreText
        )
    }

    LaunchedEffect(isDescriptionExpanded.value) {
        if (isDescriptionExpanded.value) {
            delay(300)
            bringIntoViewRequester.bringIntoView()
        }
    }

    AnnotatedText(
        text = displayedText,
        style = Theme.typography.body.small,
        maxLines = maxLine,
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .padding(top = Theme.spacing._8, bottom = Theme.spacing._8)
            .clickable (
                enabled = isDescriptionClickable.value,
                indication = null,
                interactionSource = null,
                onClick = { isDescriptionExpanded.value = !isDescriptionExpanded.value }
            )
            .animateContentSize(TweenSpec()),
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                val lastIndex = minOf(maxLine - 1, textLayoutResult.lineCount - 1)
                val lastCharIndex = textLayoutResult.getLineEnd(lastIndex, visibleEnd = true)
                visibleDescription.value = description.take(lastCharIndex).dropLast(12)
                isDescriptionClickable.value = true
            }
        }
    )
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