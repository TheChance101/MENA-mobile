package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun StatePlaceholder(
    image: Painter,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 24.dp),
            painter = image,
            contentDescription = title
        )
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = title,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.medium,
        )
        Text(
            text = description,
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.body.small,
            textAlign = TextAlign.Center
        )
    }
}