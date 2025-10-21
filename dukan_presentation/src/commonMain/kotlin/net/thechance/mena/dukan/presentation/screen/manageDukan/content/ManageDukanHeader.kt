package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.approved_dukan
import mena.dukan_presentation.generated.resources.dukan_approved_body
import mena.dukan_presentation.generated.resources.dukan_approved_header
import mena.dukan_presentation.generated.resources.edit_shelf
import mena.dukan_presentation.generated.resources.ic_package_add
import mena.dukan_presentation.generated.resources.ic_pencil_edit
import mena.dukan_presentation.generated.resources.products_count
import mena.dukan_presentation.generated.resources.shelves
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ImageWithTextContainer
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.ShelvesState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageDukanHeader(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    Column {
        AnimatedVisibility(
            visible = state.shelvesState != ShelvesState.EMPTY
        ) {
            when (state.shelvesState) {
                ShelvesState.EMPTY -> {}
                else -> Text(
                    text = stringResource(Res.string.shelves),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(
                        horizontal = Theme.spacing._16,
                        vertical = Theme.spacing._8
                    )
                )
            }
        }

        AnimatedContent(
            targetState = state.shelvesState,
            transitionSpec = {
                fadeIn(tween()) togetherWith fadeOut(tween())
            }
        ) {
            when (it) {
                ShelvesState.LOADING -> LoadingShelvesRow()

                ShelvesState.LOADED -> LoadedShelvesRow(state, listener)

                ShelvesState.EMPTY -> {
                    Column {
                        Spacer(modifier = Modifier.weight(1f))
                        NoShelvesContent()
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        ProductCountRow(
            productCount = state.totalProducts,
            listener = listener
        )
    }
}

@Composable
private fun NoShelvesContent() {
    ImageWithTextContainer(
        foregroundImageRes = Res.drawable.approved_dukan,
        header = {
            Text(
                text = stringResource(Res.string.dukan_approved_header),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                textAlign = TextAlign.Center
            )
        },
        bodyText = stringResource(Res.string.dukan_approved_body)
    )
}

@Composable
private fun LoadedShelvesRow(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            items = state.shelves,
            key = { it.id }
        ) { item ->
            Chip(
                text = item.name,
                isSelected = listener.isShelfSelected(item),
                onClick = { listener.onShelfSelected(item) },
            )
        }
    }
}

@Composable
private fun LoadingShelvesRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(count = 8) {
            Chip(
                text = "             ",
                isSelected = false,
                isEnabled = false,
                onClick = { }
            )
        }
    }
}

@Composable
private fun ProductCountRow(
    productCount: Long,
    listener: ManageDukanInteractionListener
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Theme.spacing._16, end = Theme.spacing._16, top = Theme.spacing._16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = productCount,
            transitionSpec = {
                fadeIn(tween()) togetherWith fadeOut(tween())
            }
        ) {
            Text(
                text = stringResource(Res.string.products_count, it),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeSecondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AppBarOptionContainer(
                modifier = Modifier.padding(end = Theme.spacing._4),
                onClick = { listener.onAddProductClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_package_add),
                    contentDescription = stringResource(Res.string.add_product),
                    tint = Theme.colorScheme.shadePrimary
                )
            }

            AppBarOptionContainer(
                onClick = { listener.onEditShelfClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pencil_edit),
                    contentDescription = stringResource(Res.string.edit_shelf),
                    tint = Theme.colorScheme.shadePrimary
                )
            }
        }
    }
}
