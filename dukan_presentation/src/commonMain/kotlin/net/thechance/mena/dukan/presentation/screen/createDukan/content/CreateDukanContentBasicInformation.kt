package net.thechance.mena.dukan.presentation.screen.createDukan.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.alert
import mena.dukan_presentation.generated.resources.category
import mena.dukan_presentation.generated.resources.dukan_name
import mena.dukan_presentation.generated.resources.enter_dukan_name
import mena.dukan_presentation.generated.resources.enter_your_dukan_information
import mena.dukan_presentation.generated.resources.fill_name_and_select_category
import mena.dukan_presentation.generated.resources.ic_alert_circle
import mena.dukan_presentation.generated.resources.ic_shop
import mena.dukan_presentation.generated.resources.you_can_choose_up_to_3_categories
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.chip.SelectionRow
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CreateDukanPagerContent(
    state: CreateDukanUiState,
    interactionListener: CreateDukanInteractionListener
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface),
        contentPadding = PaddingValues(
            start = Theme.spacing._16,
            end = Theme.spacing._16,
            bottom = Theme.spacing._16
        )
    ) {
        item {
            HeaderSection()
        }

        item {
            TextField(
                value = state.name,
                onValueChanged = interactionListener::onNameChanged,
                hint = stringResource(Res.string.enter_dukan_name),
                modifier = Modifier
                    .padding(bottom = Theme.spacing._12),
                leadingIcon = painterResource(Res.drawable.ic_shop),
                title = stringResource(Res.string.dukan_name),
                leadingIconTint = Theme.colorScheme.shadePrimary,
                maxCharacters = 50
            )
        }

        item {
            CategoryHeaderSection()
            SelectionRow(
                availableItems = state.dukanCategories,
                isItemSelected = interactionListener.isCategorySelected(),
                onItemClicked = interactionListener::onCategoryClicked,
                onItemEnabled = interactionListener::onCategoryEnabled
            )
        }
    }
}

@Composable
private fun HeaderSection() {
    Text(
        text = stringResource(Res.string.enter_your_dukan_information),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary,
        textAlign = TextAlign.Start
    )

    Text(
        text = stringResource(Res.string.fill_name_and_select_category),
        style = Theme.typography.body.small,
        color = Theme.colorScheme.shadeSecondary,
        modifier = Modifier
            .padding(bottom = Theme.spacing._16),
        textAlign = TextAlign.Start
    )
}

@Composable
private fun CategoryHeaderSection() {
    Text(
        text = stringResource(Res.string.category),
        style = Theme.typography.title.small,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier
            .padding(bottom = Theme.spacing._4),
        textAlign = TextAlign.Start
    )

    Row(
        modifier = Modifier
            .padding(bottom = Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(vertical = Theme.spacing._2)
                .padding(end = Theme.spacing._2)
                .align(Alignment.CenterVertically)
                .size(12.dp),
            painter = painterResource(Res.drawable.ic_alert_circle),
            contentDescription = stringResource(Res.string.alert),
            tint = Theme.colorScheme.shadeSecondary
        )
        Text(
            text = stringResource(Res.string.you_can_choose_up_to_3_categories),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
private fun CreateDukanContentBasicInformationPreview() {
    val mockState = CreateDukanUiState(
        name = "My Dukan",
        dukanCategories = listOf(
            DukanCategoryUiState("1", "Food", ""),
            DukanCategoryUiState("2", "Electronics", ""),
            DukanCategoryUiState("3", "Clothing", ""),
            DukanCategoryUiState("4", "Books", ""),
            DukanCategoryUiState("5", "Toys", ""),
        )
    )

    MenaTheme {
        CreateDukanPagerContent(
            state = mockState,
            interactionListener = PreviewCreateDukanInteractionListener
        )
    }
}

