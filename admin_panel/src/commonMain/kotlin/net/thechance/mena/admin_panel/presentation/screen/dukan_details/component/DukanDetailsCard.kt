package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import net.thechance.mena.admin_panel.presentation.component.AdminPanelContentLoading
import net.thechance.mena.admin_panel.presentation.component.OSMMapView
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.DukanDetailsScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_img
import net.thechance.mena.admin_panel.resources.dukan_location
import net.thechance.mena.admin_panel.resources.ic_dukan_placholder
import net.thechance.mena.admin_panel.resources.ic_store_location
import net.thechance.mena.admin_panel.resources.img_map_placeholder
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DukanDetailsCard(
    dukan: DukanDetailsScreenState.DukanItemUiState,
    isLoading: Boolean,
    isMapVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { AdminPanelContentLoading() }
            }

            else -> {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .clip(RoundedCornerShape(Theme.radius.md)),
                    model = dukan.imageUrl,
                    contentDescription = stringResource(Res.string.dukan_img),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.ic_dukan_placholder),
                    error = painterResource(Res.drawable.ic_dukan_placholder),
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = dukan.name,
                    style = Theme.typography.title.large,
                    color = Theme.colorScheme.shadePrimary
                )
                DukanCategories(
                    modifier = Modifier.padding(top = 2.dp),
                    categories = dukan.categories
                )
                DukanLocationMap(
                    latitude = dukan.latitude,
                    longitude = dukan.longitude,
                    isMapVisible = isMapVisible
                )
                DukanLocation(modifier = Modifier.padding(top = 8.dp), location = dukan.address)
            }
        }
    }
}

@Composable
private fun DukanCategories(
    categories: List<String>,
    modifier: Modifier = Modifier,
    dotBackgroundColor: Color = Color(0xFFD9D9D9)
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
                        .background(color = dotBackgroundColor, shape = CircleShape)
                )
            }
        }
    }
}

@Composable
private fun DukanLocationMap(
    latitude: Double,
    longitude: Double,
    isMapVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .aspectRatio(2f)
            .clip(RoundedCornerShape(Theme.radius.md)),
    ) {
        Crossfade(isMapVisible) { isVisible ->
            if (isVisible) {
                OSMMapView(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(Theme.radius.md)),
                    latitude = latitude,
                    longitude = longitude,
                    markerWidth = 45,
                    markerHeight = 58
                )
            } else {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(Res.drawable.img_map_placeholder),
                    contentDescription = null
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp),
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