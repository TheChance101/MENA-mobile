package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_img
import net.thechance.mena.admin_panel.resources.dukan_location
import net.thechance.mena.admin_panel.resources.ic_store_location
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DukanDetails(
    dukanName: String,
    dukanCategories: List<String>,
    dukanLocation: String,
    dukanImg: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .clip(RoundedCornerShape(Theme.radius.md)),
            model = dukanImg,
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(Res.string.dukan_img),
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = dukanName,
            style = Theme.typography.title.large,
            color = Theme.colorScheme.shadePrimary
        )
        DukanCategories(modifier = Modifier.padding(top = 2.dp), categories = dukanCategories)
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .aspectRatio(2f)
                .background(
                    color = Theme.colorScheme.stroke,
                    shape = RoundedCornerShape(Theme.radius.md)
                )
                .clip(RoundedCornerShape(Theme.radius.md)),
        )
        DukanLocation(modifier = Modifier.padding(top = 8.dp), location = dukanLocation)
    }
}

@Composable
private fun DukanCategories(
    categories: List<String>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        categories.forEachIndexed { index, category ->
            Text(
                text = category,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary
            )
            if (index < categories.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(3.dp)
                        .background(color = Color(0xFFD9D9D9), shape = CircleShape)
                )
            }
        }
    }
}

@Composable
private fun DukanLocation(
    location: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            modifier = Modifier.size(16.dp).padding(end = 4.dp),
            painter = painterResource(Res.drawable.ic_store_location),
            contentDescription = stringResource(Res.string.dukan_location),
            tint = Theme.colorScheme.shadeSecondary
        )
        Text(
            text = location,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )

    }

}