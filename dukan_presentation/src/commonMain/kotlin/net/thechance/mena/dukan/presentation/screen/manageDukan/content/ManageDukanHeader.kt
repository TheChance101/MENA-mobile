package net.thechance.mena.dukan.presentation.screen.manageDukan.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.shelves
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanLoadedShelvesRow
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanLoadingShelvesRow
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanNoShelvesContent
import net.thechance.mena.dukan.presentation.screen.manageDukan.component.ManageDukanProductCountRow
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.ShelvesState
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
                ShelvesState.LOADING -> ManageDukanLoadingShelvesRow()

                ShelvesState.LOADED -> ManageDukanLoadedShelvesRow(state, listener)

                ShelvesState.EMPTY -> {
                    Column {
                        Spacer(modifier = Modifier.weight(1f))
                        ManageDukanNoShelvesContent()
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        ManageDukanProductCountRow(
            productCount = state.totalProducts,
            listener = listener
        )
    }
}