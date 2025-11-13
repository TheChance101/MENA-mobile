package net.thechance.mena.wallet.presentation.component

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
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_silver
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
                .padding(bottom = 12.dp),
            painter = image,
            contentDescription = title
        )
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp),
            text = title,
            textAlign = TextAlign.Center,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.small,
        )
        Text(
            text = description,
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.body.small,
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
private fun StatePlaceholderPreview() {
    MenaTheme {
        StatePlaceholder(
            image = painterResource(Res.drawable.img_silver),
            title = "Error Title",
            description = "This is a detailed description of the error."
        )
    }
}