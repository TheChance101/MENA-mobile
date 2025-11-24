package net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.admin_panel.presentation.component.OSMMapView
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.DukanRequestsScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_img
import net.thechance.mena.admin_panel.resources.ic_dukan_placholder
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import net.thechance.mena.admin_panel.presentation.component.DukanLocation

@Composable
fun PendingDukanDetailsContent(
    selectedDukanItem: DukanRequestsScreenState.DukanItem,
    modifier: Modifier = Modifier
    ) {
    val listState = rememberLazyListState()

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            item { DukanImage(dukanImageUrl = selectedDukanItem.imageUrl) }
            item { DukanName(dukanName = selectedDukanItem.name) }
            item { DukanCategories(dukanCategories = selectedDukanItem.categories) }
            item {
                DukanLocationMap(
                    latitude = selectedDukanItem.coordinates.latitude,
                    longitude = selectedDukanItem.coordinates.longitude
                )
            }
            item {
                DukanLocation(
                    location = selectedDukanItem.address,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(listState),
            modifier = Modifier
                .padding(end = 4.dp)
                .width(4.dp)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .background(color = Theme.colorScheme.shadeTertiary)
        )
    }
}

@Composable
private fun DukanImage(dukanImageUrl: String) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.7f)
            .clip(RoundedCornerShape(16.dp)),
        model = dukanImageUrl,
        contentDescription = stringResource(Res.string.dukan_img),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(Res.drawable.ic_dukan_placholder),
        error = painterResource(Res.drawable.ic_dukan_placholder),
    )
}

@Composable
private fun DukanName(dukanName: String) {
    Text(
        text = dukanName,
        style = Theme.typography.title.large,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun DukanCategories(dukanCategories: List<String>) {
    Row(
        modifier = Modifier.padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dukanCategories.forEachIndexed { index, category ->
            Text(
                text = category,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary
            )
            if (index < dukanCategories.size - 1) {
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
private fun DukanLocationMap(latitude: Double, longitude: Double) {
    OSMMapView(
        latitude = latitude,
        longitude = longitude,
        markerWidth = 45,
        markerHeight = 58,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .aspectRatio(1.7f)
    )
}