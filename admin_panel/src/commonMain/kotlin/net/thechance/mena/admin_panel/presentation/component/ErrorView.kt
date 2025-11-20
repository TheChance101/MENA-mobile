package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ErrorView(
    image: Painter,
    title: String,
    description: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatePlaceholder(
            image = image,
            title = title,
            description = description,
        )
        PrimaryButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .heightIn(min = 48.dp)
                .fillMaxWidth(0.4f),
            text = stringResource(Res.string.retry),
            onClick = { onRetry() },
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = 16.dp
            )
        )
    }
}