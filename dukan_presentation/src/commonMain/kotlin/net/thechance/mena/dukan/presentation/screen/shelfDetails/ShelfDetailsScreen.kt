package net.thechance.mena.dukan.presentation.screen.shelfDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.shopping_basket_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.shelfDetails.components.ShelfProducts
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewShelfDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsEffects
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.shelfDetails.ShelfDetailsViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShelfDetailsScreen(
    viewModel: ShelfDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            ShelfDetailsEffects.NavigateBack -> navController.popBackStack()
            is ShelfDetailsEffects.NavigateToProductDetails -> navController.navigate(
                DukanRoute.ProductDetails(productId = effect.productId)
            )
        }
    }
    ShelfDetailsContent(
        state = state,
        listener = viewModel,
    )

}


@Composable
private fun ShelfDetailsContent(
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
) {
    OnSystemBackPressed(listener::onBackClicked)

    val dukanColor = Theme.colorScheme.primary.primary

    Scaffold(
        topBar = {
            ShelfDetailsAppBar(
                state = state,
                listener = listener,
                dukanColor = dukanColor
            )
        }
    ) {
        ShelfProducts(
            state = state,
            listener = listener
        )
    }

}

@Composable
private fun ShelfDetailsAppBar(
    state: ShelfDetailsUiState,
    listener: ShelfDetailsInteractionListener,
    dukanColor: Color,
    modifier: Modifier = Modifier
) {
    AppBar(
        modifier = modifier,
        title = state.shelfName,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        onLeadingClick = listener::onBackClicked,
        trailingContent = {
            AppBarOptionContainer(
                // when the cart contains products
                isBadgeVisible = false,
                onClick = {
                    //navigate to addToCartScreen
                },
                badgeColor = Theme.colorScheme.primary.primary
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_shopping_basket),
                    contentDescription = stringResource(Res.string.shopping_basket_icon),
                    tint = dukanColor
                )
            }
        }
    )
}

@Preview
@Composable
private fun ShelfDetailsPreview() {
    MenaTheme {
        ShelfDetailsContent(
            state = ShelfDetailsUiState(),
            listener = PreviewShelfDetailsInteractionListener,
        )
    }
}