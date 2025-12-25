package net.thechance.mena.identity.presentation.screen.profile.privacyAndPolicy.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PrivacySection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Text(
            text = title,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier
                .fillMaxWidth()

        )
        Text(
            text = content,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeTertiary,
            modifier = Modifier
                .fillMaxWidth()

        )

    }

}

@Preview(showBackground = true)
@Composable
fun PrivacySectionPreview() {
    MenaTheme {
        PrivacySection(
            title = "What is Lorem Ipsum?",
            content = "is simply dummy text of the printing and typesetting industry. " +
                    "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                    " when an unknown printer took a galley of type and scrambled it to make a type specimen book." +
                    " It has survived not only five centuries"
        )
    }
}