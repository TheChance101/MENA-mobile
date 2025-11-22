package net.thechance.mena.dukan.presentation.screen.main.components.categorySection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_view_more
import mena.dukan_presentation.generated.resources.view_more
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.CategoryCard
import net.thechance.mena.dukan.presentation.util.getScreenWidth
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState.DukanCategoryUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategorySection(
    categories: List<DukanCategoryUiState>,
    rows: Int = 2,
    onCategoryClick: (categoryID: String, categoryName: String) -> Unit,
    onViewMoreClick: () -> Unit,
    itemWidth: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    val gridLayout = calculateGridLayout(
        screenWidth = getScreenWidth(),
        categories = categories,
        rows = rows,
        itemHeight = itemWidth + 50.dp
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(gridLayout.columnsCount),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        modifier = modifier.heightIn(max = gridLayout.rowHeight)
    ) {
        items(gridLayout.visibleItems) { category ->
            CategoryCard(
                title = category.name,
                imageUrl = category.imageUrl,
                onClick = { onCategoryClick(category.id, category.name) }
            )
        }

        if (gridLayout.hasMoreItems) {
            item {
                MoreCategoryCard(
                    title = stringResource(Res.string.view_more),
                    icon = painterResource(Res.drawable.ic_view_more),
                    onClick = onViewMoreClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun CategorySectionPreview() {
    CategorySection(
        categories = fakeCategories(),
        onCategoryClick = { _, _ -> },
        onViewMoreClick = {}
    )
}

fun fakeCategories() = listOf(
    DukanCategoryUiState(
        id = "1",
        name = "Clothes",
        imageUrl = "https://example.com/images/clothes.png"
    ),
    DukanCategoryUiState(
        id = "2",
        name = "Shoes",
        imageUrl = "https://example.com/images/shoes.png"
    ),
    DukanCategoryUiState(
        id = "3",
        name = "Accessories",
        imageUrl = "https://example.com/images/accessories.png"
    ),
    DukanCategoryUiState(
        id = "4",
        name = "Furniture",
        imageUrl = "https://example.com/images/furniture.png"
    ),
    DukanCategoryUiState(
        id = "5",
        name = "Electronics",
        imageUrl = "https://example.com/images/electronics.png"
    ),
    DukanCategoryUiState(
        id = "6",
        name = "Kitchen",
        imageUrl = "https://example.com/images/kitchen.png"
    ),
    DukanCategoryUiState(
        id = "7",
        name = "Garden tools",
        imageUrl = "https://example.com/images/garden_tools.png"
    ),
    DukanCategoryUiState(
        id = "8",
        name = "Garden tools",
        imageUrl = "https://example.com/images/garden_tools.png"
    ),
)