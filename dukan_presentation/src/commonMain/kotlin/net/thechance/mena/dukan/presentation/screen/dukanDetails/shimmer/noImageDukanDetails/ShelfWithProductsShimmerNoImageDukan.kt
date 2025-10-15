package net.thechance.mena.dukan.presentation.screen.dukanDetails.shimmer.noImageDukanDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.LoadingProductsHeader
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard

@Composable
fun ShelfWithProductsShimmerNoImageDukan() {
    Column(
        Modifier.fillMaxWidth()
            .padding(top = Theme.spacing._16, start = Theme.spacing._16, end = Theme.spacing._16)
    ) {
        LoadingProductsHeader()
        Spacer(Modifier.height(Theme.spacing._8))
        LoadingProductCard(backgroundColor = Theme.colorScheme.background.surface)
        Spacer(Modifier.height(Theme.spacing._16))
        LoadingProductCard(backgroundColor = Theme.colorScheme.background.surface)
    }
}