package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.edit_shelf
import mena.dukan_presentation.generated.resources.ic_package_add
import mena.dukan_presentation.generated.resources.ic_pencil_edit
import mena.dukan_presentation.generated.resources.products_count
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageDukanProductCountRow(
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
                    tint = Theme.colorScheme.primary.primary
                )
            }

            AppBarOptionContainer(
                onClick = { listener.onEditShelfClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pencil_edit),
                    contentDescription = stringResource(Res.string.edit_shelf),
                    tint = Theme.colorScheme.primary.primary
                )
            }
        }
    }
}