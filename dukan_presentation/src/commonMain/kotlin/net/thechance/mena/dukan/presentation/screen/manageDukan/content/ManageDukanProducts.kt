package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.empty_shelf
import mena.dukan_presentation.generated.resources.empty_shelf_dark
import mena.dukan_presentation.generated.resources.shelf_empty_body
import mena.dukan_presentation.generated.resources.shelf_empty_title
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.loading.LoadingVerticalList
import net.thechance.mena.dukan.presentation.component.state.EmptyStateContent
import net.thechance.mena.dukan.presentation.navigation.LocalDarkTheme
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanProductsList
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState

@Composable
fun ManageDukanProducts(
    state: ManageDukanUiState,
    onEditProductClicked: (String) -> Unit
) {
    val product = state.products.collectAsLazyPagingItems()
    val isDark = LocalDarkTheme.current
    val icon = if(isDark) Res.drawable.empty_shelf_dark else  Res.drawable.empty_shelf


    AnimatedContent(
        targetState = product.loadState.refresh,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "ContentAnimation"
    ) { target ->
        when (target) {
            is LoadState.Loading -> {
                LoadingVerticalList { LoadingProductCard() }
            }

            is LoadState.NotLoading -> {
                if (product.itemCount == 0) {
                    EmptyStateContent(
                        image = icon,
                        title = Res.string.shelf_empty_title,
                        body = Res.string.shelf_empty_body
                    )
                } else {
                    ManageDukanProductsList(
                        products = product,
                        onEditProductClick = onEditProductClicked
                    )
                }
            }

            is LoadState.Error -> {}
        }
    }
}